package me.travis.wurstplusthree.manager;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.command.Commands;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.setting.Setting;
import me.travis.wurstplusthree.setting.type.ColourSetting;
import me.travis.wurstplusthree.setting.type.KeySetting;
import me.travis.wurstplusthree.util.Globals;
import me.travis.wurstplusthree.util.elements.WurstplusPlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConfigManager implements Globals {

    // FOLDERS
    private final String mainFolder = "Wurstplus3/";
    private final String configsFolder = mainFolder + "configs/";
    private String activeConfigFolder = configsFolder + "default/";

    // STATIC FILES
    private final String clientFile = "client.json";
    private final String configFile = "config.txt";
    private final String drawnFile = "drawn.txt";
    private final String ezFile = "ez.txt";
    private final String enemiesFile = "enemies.json";
    private final String friendsFile = "friends.json";
    private final String hudFile = "hud.json";
    private final String bindsFile = "binds.txt";
    private final String fontFile = "font.txt";

    // DIRS
    private final String clientDir = mainFolder + clientFile;
    private final String configDir = mainFolder + configFile;
    private final String drawnDir = mainFolder + drawnFile;
    private final String fontDir = mainFolder + fontFile;
    private final String ezDir = mainFolder + ezFile;
    private final String enemiesDir = mainFolder + enemiesFile;
    private final String friendsDir = mainFolder + friendsFile;

    private String currentConfigDir = mainFolder + configsFolder + activeConfigFolder;
    private String bindsDir = currentConfigDir + bindsFile;

    // FOLDER PATHS
    private final Path mainFolderPath = Paths.get(mainFolder);
    private final Path configsFolderPath = Paths.get(configsFolder);
    private Path activeConfigFolderPath = Paths.get(activeConfigFolder);

    // FILE PATHS
    private final Path clientPath = Paths.get(clientDir);
    private final Path configPath = Paths.get(configDir);
    private final Path drawnPath = Paths.get(drawnDir);
    private final Path fontPath = Paths.get(fontDir);
    private final Path ezPath = Paths.get(ezDir);
    private final Path enemiesPath = Paths.get(enemiesDir);
    private final Path friendsPath = Paths.get(friendsDir);

    private Path bindsPath = Paths.get(bindsDir);
    private Path currentConfigPath = Paths.get(currentConfigDir);

    public void loadConfig() {
        try {
            this.loadEnemies();
            this.loadFriends();
            this.loadSettings();
            this.loadBinds();
            this.loadDrawn();
            this.loadFont();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            this.verifyDir(mainFolderPath);
            this.verifyDir(configsFolderPath);
            this.verifyDir(activeConfigFolderPath);

            this.saveEnemies();
            this.saveFriends();
            this.saveSettings();
            this.saveBinds();
            this.saveDrawn();
            this.saveFont();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setActiveConfigFolder(String folder) {
        if (folder.equals(this.activeConfigFolder)) {
            return false;
        }

        this.activeConfigFolder = configsFolder + folder;
        this.activeConfigFolderPath = Paths.get(activeConfigFolder);

        this.currentConfigDir = mainFolder + configsFolder + activeConfigFolder;
        this.currentConfigPath = Paths.get(currentConfigDir);

        this.bindsDir = currentConfigDir + bindsFile;
        this.bindsPath = Paths.get(bindsDir);

        try {
            this.loadSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    // LOAD & SAVE PALS

    private void saveFriends() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.FRIEND_MANAGER.getFriends());
        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(friendsDir), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadFriends() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(friendsDir));

        WurstplusThree.FRIEND_MANAGER.setFriends(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
    }

    // LOAD & SAVE ENEMIES

    private void saveEnemies() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(WurstplusThree.ENEMY_MANAGER.getEnemies());

        OutputStreamWriter file;

        file = new OutputStreamWriter(new FileOutputStream(enemiesDir), StandardCharsets.UTF_8);
        file.write(json);
        file.close();
    }

    private void loadEnemies() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(enemiesDir));

        WurstplusThree.ENEMY_MANAGER.setEnemies(gson.fromJson(reader, new TypeToken<ArrayList<WurstplusPlayer>>(){}.getType()));

        reader.close();
    }

    // LOAD & SAVE SETTINGS

    private void saveSettings() throws IOException {
        for (Hack hack : WurstplusThree.HACKS.getHacks()) {
            String fileName = activeConfigFolder + hack.getName() + ".txt";
            Path filePath = Paths.get(fileName);
            this.deleteFile(fileName);
            verifyFile(filePath);

            File file = new File(fileName);
            BufferedWriter br = new BufferedWriter(new FileWriter(file));

            for (Setting setting : hack.getSettings()) {
                if (setting instanceof ColourSetting) {
                    ColourSetting color = (ColourSetting) setting;
                    br.write(setting.getName() + ":" + color.getValue().getRed() + ":" + color.getValue().getGreen()
                            + ":" + color.getValue().getBlue() + ":" + color.getValue().getAlpha() + ":"
                            + color.getRainbow() + "\r\n");
                }
                else if(setting instanceof KeySetting){
                    KeySetting key = (KeySetting) setting;
                    br.write(setting.getName()+ ":" + key.getKey() + "\r\n");
                }
                else {
                    br.write(setting.getName() + ":" + setting.getValue() + "\r\n");
                }
            }

            br.close();
        }
    }

    private void loadSettings() throws IOException {
        for (Hack hack : WurstplusThree.HACKS.getHacks()) {
            String file_name = activeConfigFolder + hack.getName() + ".txt";
            File file = new File(file_name);
            if (!file.exists()) continue;
            FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
            DataInputStream di_stream = new DataInputStream(fi_stream);
            BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));

            String line;
            while ((line = br.readLine()) != null) {

                String colune = line.trim();
                String name = colune.split(":")[0];
                String value = colune.split(":")[1];

                Setting setting = hack.getSettingByName(name);
                if (setting == null) continue;
                switch (setting.getType()) {
                    case "boolean":
                        setting.setValue(Boolean.parseBoolean(value));
                        break;
                    case "colour":
                        try {
                            ColourSetting colourSetting = (ColourSetting) setting;
                            int red = Integer.parseInt(value);
                            int green = Integer.parseInt(colune.split(":")[2]);
                            int blue = Integer.parseInt(colune.split(":")[3]);
                            int alpha = Integer.parseInt(colune.split(":")[4]);
                            boolean rainbow = Boolean.parseBoolean(colune.split(":")[5]);
                            colourSetting.setRainbow(rainbow);
                            colourSetting.setValue(red, green, blue, alpha);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "double":
                        setting.setValue(Double.parseDouble(value));
                        break;
                    case "enum":
                        setting.setValue(value);
                        break;
                    case "int":
                        setting.setValue(Integer.parseInt(value));
                        break;
                    case "key":
                        KeySetting key = (KeySetting) setting;
                        key.setKey(Integer.parseInt(value));
                        break;
                }
            }

            br.close();
        }
    }

    // LOAD & SAVE BINDS

    private void saveBinds() throws IOException {
        final String file_name = activeConfigFolder + "BINDS.txt";
        final Path file_path = Paths.get(file_name);

        this.deleteFile(file_name);
        this.verifyFile(file_path);
        final File file = new File(file_name);
        final BufferedWriter br = new BufferedWriter(new FileWriter(file));
        br.write(Commands.prefix + "\r\n");
        for (Hack module : WurstplusThree.HACKS.getHacks()) {
            br.write(module.getName() + ":" + module.getBind() + ":" + module.isEnabled() + "\r\n");
        }
        br.close();
    }

    private void loadBinds() throws IOException {
        final String file_name = activeConfigFolder + "BINDS.txt";
        final File file = new File(file_name);
        final FileInputStream fi_stream = new FileInputStream(file.getAbsolutePath());
        final DataInputStream di_stream = new DataInputStream(fi_stream);
        final BufferedReader br = new BufferedReader(new InputStreamReader(di_stream));
        boolean flag = true;
        String line;
        while ((line = br.readLine()) != null) {
            try {
                if (flag) {
                    Commands.prefix = line;
                    flag = false;
                } else {
                    final String colune = line.trim();
                    final String tag = colune.split(":")[0];
                    final String bind = colune.split(":")[1];
                    final String active = colune.split(":")[2];
                    Hack hack = WurstplusThree.HACKS.getHackByName(tag);
                    hack.setBind(Integer.parseInt(bind));
                    if (Boolean.parseBoolean(active)) {
                        hack.enable();
                    }
                }

            } catch (Exception ignored) {}
        }
        br.close();
    }

    private void saveDrawn() throws IOException {
        FileWriter writer = new FileWriter(drawnDir);
        for (Hack hack : WurstplusThree.HACKS.getDrawnHacks()) {
            writer.write(hack.getName() + System.lineSeparator());
        }
        writer.close();
    }

    private void loadDrawn() throws IOException {
        for (String hackName : Files.readAllLines(drawnPath)) {
            WurstplusThree.HACKS.addDrawHack(WurstplusThree.HACKS.getHackByName(hackName));
        }
    }

    private void saveFont() throws IOException {
        FileWriter writer = new FileWriter(fontDir);
        writer.write(WurstplusThree.GUI_FONT_MANAGER.fontName + System.lineSeparator());
        writer.write(WurstplusThree.GUI_FONT_MANAGER.fontSize + System.lineSeparator());
        writer.close();
    }

    private void loadFont() throws IOException {
        boolean flag = true;
        for (String line : Files.readAllLines(fontPath)) {
            if (flag) {
                WurstplusThree.GUI_FONT_MANAGER.setFont(line);
                flag = false;
            } else {
                WurstplusThree.GUI_FONT_MANAGER.setFontSize(Integer.parseInt(line));
                return;
            }
        }
        WurstplusThree.GUI_FONT_MANAGER.setFont();
    }

    public boolean deleteFile(final String path) throws IOException {
        final File f = new File(path);
        return f.delete();
    }

    public void verifyFile(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    public void verifyDir(final Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
    }

}
