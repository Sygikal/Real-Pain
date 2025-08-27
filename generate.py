import os
import collections.abc
import requests
import json
import xml.etree.ElementTree as ET

currentdir = os.getcwd()

#latest_loaders = requests.get(f'https://meta.fabricmc.net/v2/versions/loader?limit=1', allow_redirects=True).json()
#latest_loader = latest_loaders[0]['version']
#print(loaders)

#jdlist = requests.get('https://maven.fabricmc.net/jdlist.txt', allow_redirects=True)

maven_group = input("Maven Group (eg com.google): ")
archives_base_name = input("Archive Base Name (Part after maven group eg com.google.gson): ")
mod_id = input("Mod ID (lowercase): ")
mod_name = input("Mod Name: ")
versions_input = input("Enter Versions (Comma separated, no spaces): ")
versions = versions_input.split(',')

for version in versions:
    print(f"  {versions.index(version)}. {version}")
try:
    selected = int(input(f"\nWhich version do you want to be the default? (0-{len(versions)-1}): "))
except ValueError:
    print("Please enter a number!")
    exit()

default_version = versions[selected]

version_string = ''
for vvv in versions:
    version_string += f"\"{vvv}\""
    if versions.index(vvv) != (len(versions)-1):
        version_string += ", "

split_maven_name = maven_group.split('.')
main_file_name = "Main"

filesystem = {
    'src/': {
        'main/': {
            'java/': {
                f"{maven_group.replace('.','/')}/": {
                    f'{archives_base_name}/': {
                        'mixin/': {
                            'ExampleMixin.java': [
                                f"package {maven_group}.{archives_base_name}.mixin;",
                                "",
                                "import net.minecraft.server.MinecraftServer;",
                                "import org.spongepowered.asm.mixin.Mixin;",
                                "import org.spongepowered.asm.mixin.injection.At;",
                                "import org.spongepowered.asm.mixin.injection.Inject;",
                                "import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;",
                                "",
                                "@Mixin(MinecraftServer.class)",
                                "public class ExampleMixin {",
                                "	@Inject(at = @At(\"HEAD\"), method = \"loadWorld\")",
                                "	private void init(CallbackInfo info) {",
                                "		// This code is injected into the start of MinecraftServer.loadWorld()V",
                                "	}",
                                "}"
                            ]
                        },
                        f'{main_file_name}.java': [
                            f"package {maven_group}.{archives_base_name};",
                            "",
                            "import net.fabricmc.api.ModInitializer;",
                            "",
                            "import org.slf4j.Logger;",
                            "import org.slf4j.LoggerFactory;",
                            "",
                            f"public class {main_file_name} implements ModInitializer " + "{",
                            f"	public static final String MOD_ID = \"{mod_id}\";",
                            "",
                            "	// This logger is used to write text to the console and the log file.",
                            "	// It is considered best practice to use your mod id as the logger's name.",
                            "	// That way, it's clear which mod wrote info, warnings, and errors.",
                            "	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);",
                            "",
                            "	@Override",
                            "	public void onInitialize() {",
                            "		// This code runs as soon as Minecraft is in a mod-load-ready state.",
                            "		// However, some things (like resources) may still be uninitialized.",
                            "		// Proceed with mild caution.",
                            "",
                            "		LOGGER.info(\"Hello Fabric world!\");",
                            "	}",
                            "}"
                        ]
                    }
                }
            },
            'resources/': {
                'assets/': {
                    f'{mod_id}/': {
                        "icon.png": "https://upload.wikimedia.org/wikipedia/commons/7/70/Example.png"
                    }
                },
                f'{mod_id}.mixins.json': {
                    "required": True,
                    "package": f"{maven_group}.{archives_base_name}.mixin",
                    "compatibilityLevel": "JAVA_17",
                    "mixins": [
                        "ExampleMixin"
                    ],
                    "injectors": {
                        "defaultRequire": 1
                    }
                },
                'fabric.mod.json': {
                    "schemaVersion": 1,
                    "id": f"{mod_id}",
                    "version": "${version}",
                    "name": f"{mod_name}",
                    "description": "This is an example description! Tell everyone what your mod is about!",
                    "authors": [
                        "Me!"
                    ],
                    "contact": {
                        "homepage": "https://fabricmc.net/",
                        "sources": "https://github.com/FabricMC/fabric-example-mod"
                    },
                    "license": "CC0-1.0",
                    "icon": f"assets/{mod_id}/icon.png",
                    "environment": "*",
                    "entrypoints": {
                        "main": [
                            f"{maven_group}.{archives_base_name}.{main_file_name}"
                        ]
                    },
                    "accessWidener": "${aw}",
                    "mixins": [
                        f"{mod_id}.mixins.json"
                    ],
                    "depends": {
                        "fabricloader": ">=0.16.14",
                        "minecraft": "${mcdep}",
                        "java": "${javadep}",
                        "fabric-api": "*"
                    },
                    "suggests": {
                        "another-mod": "*"
                    }
                }
            }
        }
    },
    'versioned_gradles/': {},
    'gradle.properties': [
        "org.gradle.jvmargs=-Xmx1G",
        "org.gradle.parallel=true",
        "",
        "loader_version=0.16.14",
        "loom_version=1.10-SNAPSHOT",
        "",
        "mod_version=1.0.0",
        f"maven_group={maven_group}",
        f"archives_base_name={archives_base_name}",
        "",
        "#This file is the default gradle properties",
        "#If you would like to override based on version add it to /versions/xxx/gradle.properties",
        "#and set to [VERSIONED] if u dont want default",
        "",
        "minecraft_version=[VERSIONED]",
        "yarn_mappings=[VERSIONED]",
        "fabric_version=[VERSIONED]"
    ],
    'settings.gradle': [
        "pluginManagement {",
        "    repositories {",
        "        maven {",
        "            name = 'Fabric'",
        "            url = 'https://maven.fabricmc.net/'",
        "        }",
        "        mavenCentral()",
        "        gradlePluginPortal()",
        "    }",
        "}",
        "",
        "plugins {",
        "    id(\"dev.kikugie.stonecutter\") version \"0.6.1\"",
        "}",
        "",
        "stonecutter {",
        "    kotlinController = false",
        "    centralScript = \"build.gradle\"",
        "",    
        "    create(getRootProject()) {",
        f"        versions {version_string}",
        f"        vcsVersion = \"{default_version}\"",
        "    }",
        "}"
    ]
}

def iterate(array, path=""):
    for x, v in array.items():
        if isinstance(v, dict) and (x.endswith('/')):
            sexpath = path + x
            if not os.path.exists(f'{sexpath}'): os.makedirs(f'{sexpath}');
            if v:
                iterate(v, sexpath)
        else:
            if not os.path.isfile(f'{path}{x}'):
                if isinstance(v, list):
                    #print(v)
                    with open(f'{path}{x}', 'wb') as the_file:
                        for line in v:
                            content = b''
                            content = bytes(f'{line}\n', 'utf-8')
                            the_file.write(content)
                elif isinstance(v, dict):
                    file = open(f'{path}{x}', 'w')
                    json.dump(v, file, ensure_ascii=True, indent=4)
                    file.close()
                else:
                    content = b''
                    if 'http' in v:
                        content = requests.get(f'{v}', allow_redirects=True).content
                    else:
                        print(v)
                        content = bytes(v, 'utf-8')
                    open(f'{path}{x}', 'wb').write(content)

version_meta = requests.get('https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml', allow_redirects=True).content
doc = ET.fromstring(version_meta)

for version in versions:
    # Get relavent fabric-api verisons
    api_versions = []
    for tag in doc.iter('version'):
        if tag.text.endswith(version):
            api_versions.append(tag.text)

    yarn = requests.get(f'https://meta.fabricmc.net/v2/versions/yarn/{version}?limit=1', allow_redirects=True).json()[0]
    if yarn['gameVersion'] == version:
        filesystem['versioned_gradles/'][f'{version}/'] = {'gradle.properties': []}
        filesystem['versioned_gradles/'][f'{version}/']['gradle.properties'] = [
            f"minecraft_version={version}",
            f"yarn_mappings={yarn['version']}",
            "#You will have to edit and test this",
            f"supported_mc_versions=~{version}",
            "",
            f"fabric_version={api_versions[-1]}"
        ]

        filesystem['src/']['main/']['resources/'][f'{version}.accesswidener'] = [
            "accessWidener v2 named"
        ]


iterate(filesystem, os.getcwd() +  "/")