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

final class BlacklistForce {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final List<String> REMOVE = List.of(
            "tacz:.*",
            "tacz:ammo",
            "tacz:attachment",
            "tacz:ammo_box",
            "tacz:gun_smith_table",
            "tacz:workbench_a",
            "tacz:workbench_b",
            "tacz:workbench_c",
            "tacz:target",
            "tacz:target_minecart",
            "tacz:statue"
    );

    private static final List<String> IDS = List.of(
            "tacz:modern_kinetic_gun",
            "powergrid:portable_saw",
            "powergrid:portable_drill",
            "powergrid:electrozapper",
            "create:handheld_worldshaper",
            "create:extendo_grip",
            "create:potato_cannon",
            "simulated:plunger_launcher",
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

    private static final String CONFIG = "punchy/punchy_config.json";

    private BlacklistForce() {
    }

    static void apply() {
        int files = patchFile();
        int memory = patchLoaded();
        if (files > 0 || memory > 0) {
            DontPunchMyTacz.LOGGER.info("Updated Punchy blacklist");
        }
    }

    private static int patchFile() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(CONFIG);
        if (!Files.isRegularFile(path)) {
            return 0;
        }

        JsonObject root;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed == null || !parsed.isJsonObject()) {
                return 0;
            }
            root = parsed.getAsJsonObject();
        } catch (Exception e) {
            DontPunchMyTacz.LOGGER.warn("Failed to read {}", path.getFileName());
            return 0;
        }

        if (!root.has("itemBlacklist") && !root.has("enableMod")) {
            return 0;
        }
        if (!merge(root)) {
            return 0;
        }

        Path temp = path.resolveSibling(path.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
            writer.write('\n');
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            return 1;
        } catch (Exception e) {
            DontPunchMyTacz.LOGGER.warn("Failed to write {}", path.getFileName());
            try {
                Files.deleteIfExists(temp);
            } catch (Exception ignored) {
            }
            return 0;
        }
    }

    private static boolean merge(JsonObject root) {
        JsonArray list = root.has("itemBlacklist") && root.get("itemBlacklist").isJsonArray()
                ? root.getAsJsonArray("itemBlacklist")
                : new JsonArray();
        JsonObject dual = root.has("blacklistApplyDualHanded") && root.get("blacklistApplyDualHanded").isJsonObject()
                ? root.getAsJsonObject("blacklistApplyDualHanded")
                : new JsonObject();

        Set<String> have = new LinkedHashSet<>();
        for (JsonElement el : list) {
            if (el.isJsonPrimitive()) {
                have.add(el.getAsString());
            }
        }

        boolean changed = false;
        for (String id : REMOVE) {
            if (have.remove(id)) {
                changed = true;
            }
            if (dual.has(id)) {
                dual.remove(id);
                changed = true;
            }
        }

        JsonArray next = new JsonArray();
        for (String id : have) {
            next.add(id);
        }
        for (String id : IDS) {
            if (have.add(id)) {
                next.add(id);
                changed = true;
            }
            if (!dual.has(id)) {
                dual.addProperty(id, false);
                changed = true;
            }
        }

        root.add("itemBlacklist", next);
        root.add("blacklistApplyDualHanded", dual);
        return changed;
    }

    private static int patchLoaded() {
        if (!ModList.get().isLoaded("punchy")) {
            return 0;
        }
        Object mod = ModList.get().getModContainerById("punchy").map(c -> {
            try {
                return c.getClass().getMethod("getMod").invoke(c);
            } catch (Exception e) {
                return null;
            }
        }).orElse(null);
        return mod == null ? 0 : scan(mod, 0);
    }

    private static int scan(Object target, int depth) {
        if (target == null || depth > 2) {
            return 0;
        }

        int hits = 0;
        for (Field field : target.getClass().getDeclaredFields()) {
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(target);
            } catch (Exception e) {
                continue;
            }
            if (value == null) {
                continue;
            }

            String name = field.getName();
            if ("itemBlacklist".equals(name) && value instanceof Collection<?> col) {
                if (addIds(col)) {
                    hits++;
                }
            } else if ("blacklistApplyDualHanded".equals(name) && value instanceof Map<?, ?> map) {
                if (addFlags(map)) {
                    hits++;
                }
            } else if (depth == 0) {
                String type = value.getClass().getName();
                if (type.toLowerCase().contains("punchy") && !type.startsWith("java.")) {
                    hits += scan(value, 1);
                }
            }
        }
        return hits;
    }

    @SuppressWarnings("unchecked")
    private static boolean addIds(Collection<?> collection) {
        if (!collection.isEmpty() && !(collection.iterator().next() instanceof String)) {
            return false;
        }
        Collection<Object> col = (Collection<Object>) collection;
        boolean changed = false;
        for (String id : REMOVE) {
            if (col.remove(id)) {
                changed = true;
            }
        }
        for (String id : IDS) {
            if (!col.contains(id)) {
                try {
                    col.add(id);
                    changed = true;
                } catch (Exception e) {
                    return changed;
                }
            }
        }
        return changed;
    }

    @SuppressWarnings("unchecked")
    private static boolean addFlags(Map<?, ?> map) {
        Map<Object, Object> m = (Map<Object, Object>) map;
        boolean changed = false;
        for (String id : REMOVE) {
            if (m.remove(id) != null) {
                changed = true;
            }
        }
        for (String id : IDS) {
            if (!m.containsKey(id)) {
                try {
                    m.put(id, Boolean.FALSE);
                    changed = true;
                } catch (Exception e) {
                    return changed;
                }
            }
        }
        return changed;
    }
}
