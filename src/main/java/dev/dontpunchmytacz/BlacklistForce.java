package dev.dontpunchmytacz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Same thing as pressing F8 in Punchy and blacklisting TACZ.
 * Only adds missing ids. Never wipes other Punchy settings.
 */
final class BlacklistForce {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // Regex covers the whole TACZ mod. The rest is the F8 list that already worked.
    private static final List<String> IDS = List.of(
            "tacz:.*",
            "tacz:modern_kinetic_gun",
            "tacz:ammo",
            "tacz:attachment",
            "tacz:ammo_box",
            "tacz:gun_smith_table",
            "tacz:workbench_a",
            "tacz:workbench_b",
            "tacz:workbench_c",
            "tacz:target",
            "tacz:target_minecart",
            "tacz:statue",
            "tacz:aa12",
            "tacz:ai_awp",
            "tacz:ak47",
            "tacz:aug",
            "tacz:b93r",
            "tacz:cz75",
            "tacz:db_long",
            "tacz:db_short",
            "tacz:deagle",
            "tacz:deagle_golden",
            "tacz:fn_evolys",
            "tacz:fn_fal",
            "tacz:g36k",
            "tacz:glock_17",
            "tacz:hk416d",
            "tacz:hk_g3",
            "tacz:hk_mk23",
            "tacz:hk_mp5a5",
            "tacz:kar98",
            "tacz:lonetrail",
            "tacz:m1014",
            "tacz:m107",
            "tacz:m16a1",
            "tacz:m16a4",
            "tacz:m1911",
            "tacz:m249",
            "tacz:m320",
            "tacz:m4a1",
            "tacz:m700",
            "tacz:m870",
            "tacz:m95",
            "tacz:m9a4",
            "tacz:minigun",
            "tacz:mk14",
            "tacz:p320",
            "tacz:p90",
            "tacz:qbz_191",
            "tacz:qbz_95",
            "tacz:rhino357",
            "tacz:rpg7",
            "tacz:rpk",
            "tacz:scar_h",
            "tacz:scar_l",
            "tacz:sks_tactical",
            "tacz:spas_12",
            "tacz:spr15hb",
            "tacz:springfield1873",
            "tacz:taurus500",
            "tacz:taurus943",
            "tacz:timeless50",
            "tacz:type_81",
            "tacz:ump45",
            "tacz:uzi",
            "tacz:vector45"
    );

    private static final String[] FILES = {
            "punchy/punchy_config.json"
    };

    private BlacklistForce() {
    }

    static void apply() {
        int files = patchFiles();
        int memory = patchLoadedPunchy();
        if (files > 0 || memory > 0) {
            DontPunchMyTacz.LOGGER.info("Told Punchy to leave TACZ alone (config {}, memory {}).", files, memory);
        }
    }

    private static int patchFiles() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        int changed = 0;
        for (String name : FILES) {
            Path path = configDir.resolve(name);
            if (Files.isRegularFile(path) && patchFile(path)) {
                changed++;
            }
        }
        return changed;
    }

    private static boolean patchFile(Path path) {
        JsonObject root;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed == null || !parsed.isJsonObject()) {
                return false;
            }
            root = parsed.getAsJsonObject();
        } catch (Exception ignored) {
            DontPunchMyTacz.LOGGER.warn("Could not read {}", path.getFileName());
            return false;
        }

        // Real Punchy settings file only. Leave tuning dumps alone.
        if (!root.has("itemBlacklist") && !root.has("enableMod")) {
            return false;
        }
        if (!merge(root)) {
            return false;
        }

        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
            writer.write('\n');
        } catch (Exception ignored) {
            DontPunchMyTacz.LOGGER.warn("Could not write {}", path.getFileName());
            deleteQuietly(temp);
            return false;
        }

        try {
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception ignored) {
            try {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception failed) {
                DontPunchMyTacz.LOGGER.warn("Could not replace {}", path.getFileName());
                deleteQuietly(temp);
                return false;
            }
        }
        return true;
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    private static boolean merge(JsonObject root) {
        JsonArray list = jsonArray(root, "itemBlacklist");
        JsonObject dual = jsonObject(root, "blacklistApplyDualHanded");

        Set<String> already = new LinkedHashSet<>();
        for (JsonElement element : list) {
            if (element.isJsonPrimitive()) {
                already.add(element.getAsString());
            }
        }

        boolean changed = false;
        for (String id : IDS) {
            if (already.add(id)) {
                list.add(id);
                changed = true;
            }
            if (!dual.has(id)) {
                dual.addProperty(id, false);
                changed = true;
            }
        }

        root.add("itemBlacklist", list);
        root.add("blacklistApplyDualHanded", dual);
        return changed;
    }

    private static JsonArray jsonArray(JsonObject root, String key) {
        if (root.has(key) && root.get(key).isJsonArray()) {
            return root.getAsJsonArray(key);
        }
        return new JsonArray();
    }

    private static JsonObject jsonObject(JsonObject root, String key) {
        if (root.has(key) && root.get(key).isJsonObject()) {
            return root.getAsJsonObject(key);
        }
        return new JsonObject();
    }

    /**
     * If Punchy already read the file, add the same ids to the two F8 fields.
     * Those two names only.
     */
    private static int patchLoadedPunchy() {
        if (!ModList.get().isLoaded("punchy")) {
            return 0;
        }
        Object mod = punchyInstance();
        if (mod == null) {
            return 0;
        }
        return fillKnownFields(mod, 0);
    }

    private static Object punchyInstance() {
        return ModList.get().getModContainerById("punchy").map(container -> {
            try {
                return container.getClass().getMethod("getMod").invoke(container);
            } catch (Exception ignored) {
                return null;
            }
        }).orElse(null);
    }

    private static int fillKnownFields(Object target, int depth) {
        if (target == null || depth > 2) {
            return 0;
        }

        int hits = 0;
        for (Field field : target.getClass().getDeclaredFields()) {
            String name = field.getName();
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(target);
            } catch (Exception ignored) {
                continue;
            }
            if (value == null) {
                continue;
            }

            if ("itemBlacklist".equals(name) && value instanceof Collection<?> collection) {
                if (addStrings(collection)) {
                    hits++;
                }
            } else if ("blacklistApplyDualHanded".equals(name) && value instanceof Map<?, ?> map) {
                if (addFlags(map)) {
                    hits++;
                }
            } else if (depth == 0 && isPunchyConfigObject(value)) {
                hits += fillKnownFields(value, depth + 1);
            }
        }
        return hits;
    }

    private static boolean isPunchyConfigObject(Object value) {
        String type = value.getClass().getName();
        return type.toLowerCase().contains("punchy") && !type.startsWith("java.");
    }

    @SuppressWarnings("unchecked")
    private static boolean addStrings(Collection<?> collection) {
        if (!collection.isEmpty() && !(collection.iterator().next() instanceof String)) {
            return false;
        }
        Collection<Object> sink = (Collection<Object>) collection;
        boolean changed = false;
        for (String id : IDS) {
            if (!sink.contains(id)) {
                try {
                    sink.add(id);
                    changed = true;
                } catch (Exception ignored) {
                    return changed;
                }
            }
        }
        return changed;
    }

    @SuppressWarnings("unchecked")
    private static boolean addFlags(Map<?, ?> map) {
        Map<Object, Object> sink = (Map<Object, Object>) map;
        boolean changed = false;
        for (String id : IDS) {
            if (!sink.containsKey(id)) {
                try {
                    sink.put(id, Boolean.FALSE);
                    changed = true;
                } catch (Exception ignored) {
                    return changed;
                }
            }
        }
        return changed;
    }
}
