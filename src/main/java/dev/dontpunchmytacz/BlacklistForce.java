package dev.dontpunchmytacz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class BlacklistForce {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static final List<String> REMOVE = List.of(
            "tacz:.*",
            "superbwarfare:.*",
            "lrtactical:.*",
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

    private static final List<String> TACZ_IDS = List.of(
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

    private static final List<String> SUPERB_WARFARE_IDS = List.of(
            "superbwarfare:glock_17",
            "superbwarfare:glock_18",
            "superbwarfare:mp_443",
            "superbwarfare:m_1911",
            "superbwarfare:trachelium",
            "superbwarfare:mp_5",
            "superbwarfare:vector",
            "superbwarfare:ak_47",
            "superbwarfare:ak_12",
            "superbwarfare:sks",
            "superbwarfare:m_4",
            "superbwarfare:hk_416",
            "superbwarfare:qbz_95",
            "superbwarfare:qbz_191",
            "superbwarfare:insidious",
            "superbwarfare:mk_14",
            "superbwarfare:ql_1031",
            "superbwarfare:marlin",
            "superbwarfare:k_98",
            "superbwarfare:mosin_nagant",
            "superbwarfare:svd",
            "superbwarfare:awm",
            "superbwarfare:m_98b",
            "superbwarfare:sentinel",
            "superbwarfare:hunting_rifle",
            "superbwarfare:ntw_20",
            "superbwarfare:homemade_shotgun",
            "superbwarfare:m_870",
            "superbwarfare:aa_12",
            "superbwarfare:devotion",
            "superbwarfare:rpk",
            "superbwarfare:m_60",
            "superbwarfare:m_2_hb",
            "superbwarfare:minigun",
            "superbwarfare:m_79",
            "superbwarfare:secondary_cataclysm",
            "superbwarfare:rpg",
            "superbwarfare:javelin",
            "superbwarfare:igla_9k38",
            "superbwarfare:bocek",
            "superbwarfare:super_star_shooter",
            "superbwarfare:nail_gun",
            "superbwarfare:repair_tool",
            "superbwarfare:taser",
            "superbwarfare:reforging",
            "superbwarfare:beast_gun_test",
            "superbwarfare:hand_grenade",
            "superbwarfare:rgo_grenade",
            "superbwarfare:m18_smoke_grenade",
            "superbwarfare:lunge_mine",
            "superbwarfare:skin_spray",
            "superbwarfare:hammer",
            "superbwarfare:golden_hammer",
            "superbwarfare:steel_hammer",
            "superbwarfare:diamond_hammer",
            "superbwarfare:cemented_carbide_hammer",
            "superbwarfare:netherite_hammer"
    );
    
    private static final List<String> LES_RAISINS_IDS = List.of(
            "lrtactical:melee",
            "lrtactical:throwable",
            "lrtactical:consumable",
            "lrtactical:flash_shield"
    );

    private static final List<String> MISC_IDS = List.of(
            "createdieselgenerators:chemical_sprayer",
            "createdieselgenerators:chemical_sprayer_lighter",
            "exposure:camera"
    );
    
    private static final List<String> IDS = Stream.of(TACZ_IDS, SUPERB_WARFARE_IDS, LES_RAISINS_IDS, MISC_IDS)
            .flatMap(Collection::stream)
            .toList();

    private static final String CONFIG = "punchy/punchy_config.json";

    private static final String DEFAULT_CONFIG = """
            {
              "renderArmorArmsFP": true,
              "interactiveStuffCompat": false,
              "bettercombatCompat": false,
              "swordBlockingCompat": false,
              "animationSpeed": 5.5,
              "enableMod": true,
              "enableTuning": false,
              "disableResourcePackModelParts": false,
              "disableArmPhysics": false,
              "disableNativeItemPhysics": false,
              "disableBoatMinecartRaftModels": false,
              "disablePistonModels": false,
              "disableChestModels": false,
              "disableEnchantingTableModels": false,
              "disableBoatFirstPersonAnimations": false,
              "disableEnhancedFireArmEffects": false,
              "enableCrawlAnimation": true,
              "enableClimbAnimation": true,
              "enableSwimAnimation": true,
              "enableElytraFlightAnimation": true,
              "disableNewKeyframeMoves": true,
              "enableCustomWalk": true,
              "enableSprintArmSwing": true,
              "enableFallArmAnimation": true,
              "sprintArmSwingIntensity": 1.0,
              "sprintArmSwingSpeed": 0.0,
              "enableFreezeShake": true,
              "enableFreezeArmOverlay": true,
              "enableMud": true,
              "enableSweat": true,
              "enableBurn": true,
              "enableParticles": true,
              "enableModelPartsGlow": true,
              "enableBlurGlowAtDark": true,
              "firstPersonModelHideEnabled": true,
              "firstPersonModelHidePitch": 60.0,
              "itemBlacklist": [],
              "blacklistApplyDualHanded": {},
              "animationPackSelections": {},
              "animationPackTypeSelections": {},
              "mixSpecificForceOverride": {}
            }
            """;

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
        Path path = FabricLoader.getInstance().getConfigDir().resolve(CONFIG);
        JsonObject root;
        if (Files.isRegularFile(path)) {
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
        } else {
            root = JsonParser.parseString(DEFAULT_CONFIG).getAsJsonObject();
        }

        if (!merge(root)) {
            return 0;
        }

        try {
            Files.createDirectories(path.getParent());
        } catch (Exception e) {
            DontPunchMyTacz.LOGGER.warn("Failed to create {}", path.getParent().getFileName());
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

        boolean changed = false;

        for (String id : REMOVE) {
            for (int i = list.size() - 1; i >= 0; i--) {
                JsonElement el = list.get(i);
                if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString() && el.getAsString().equals(id)) {
                    list.remove(i);
                    changed = true;
                }
            }
            if (dual.remove(id) != null) {
                changed = true;
            }
        }

        Set<String> present = new LinkedHashSet<>();
        for (JsonElement el : list) {
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                present.add(el.getAsString());
            }
        }

        for (String id : IDS) {
            if (present.add(id)) {
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

    private static int patchLoaded() {
        if (!FabricLoader.getInstance().isModLoaded("punchy")) {
            return 0;
        }
        ClassLoader loader = BlacklistForce.class.getClassLoader();
        for (String name : classNames()) {
            Class<?> type;
            try {
                type = Class.forName(name, false, loader);
            } catch (Throwable ignored) {
                continue;
            }
            int hits = scan(type, 0);
            if (hits > 0) {
                return hits;
            }
        }
        return 0;
    }

    private static List<String> classNames() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer("punchy");
        if (container.isEmpty()) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        for (Path root : container.get().getRootPaths()) {
            collect(root, names);
        }
        return names;
    }

    private static void collect(Path root, List<String> names) {
        try {
            if (Files.isDirectory(root)) {
                try (Stream<Path> walk = Files.walk(root)) {
                    walk.filter(p -> p.toString().endsWith(".class"))
                            .forEach(p -> names.add(toBinaryName(root.relativize(p).toString())));
                }
            } else if (Files.isRegularFile(root)) {
                try (ZipFile zip = new ZipFile(root.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.endsWith(".class")) {
                            names.add(toBinaryName(name));
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static String toBinaryName(String path) {
        String name = path.endsWith(".class") ? path.substring(0, path.length() - 6) : path;
        return name.replace('/', '.');
    }

    private static int scan(Object target, int depth) {
        if (target == null || depth > 3) {
            return 0;
        }
        boolean isClass = target instanceof Class<?>;
        Class<?> type = isClass ? (Class<?>) target : target.getClass();

        int hits = 0;
        for (Field field : type.getDeclaredFields()) {
            if (isClass && !Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Object value;
            try {
                field.setAccessible(true);
                value = field.get(isClass ? null : target);
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
            } else if (depth < 3) {
                String valueType = value.getClass().getName();
                if (valueType.startsWith("punchy.")) {
                    hits += scan(value, depth + 1);
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
