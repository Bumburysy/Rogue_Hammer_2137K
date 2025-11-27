package project.roguelike.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    public static final Music musicMenu = Gdx.audio.newMusic(Gdx.files.internal("sounds/music_menu.mp3"));
    public static final Music musicBoss = Gdx.audio.newMusic(Gdx.files.internal("sounds/music_boss.mp3"));
    public static final Music musicWin = Gdx.audio.newMusic(Gdx.files.internal("sounds/music_win.mp3"));
    public static final Music musicLoose = Gdx.audio.newMusic(Gdx.files.internal("sounds/music_loose.mp3"));
    public static final Sound soundGunShot = Gdx.audio.newSound(Gdx.files.internal("sounds/gun_shot.wav"));
    public static final Sound soundReload = Gdx.audio.newSound(Gdx.files.internal("sounds/reload.wav"));
    public static final Sound soundButtonClick = Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.wav"));
    public static final Sound soundPlayerHit = Gdx.audio.newSound(Gdx.files.internal("sounds/player_hit.wav"));
    public static final Sound soundPlayerDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/player_death.wav"));
    public static final Sound soundItemPickup = Gdx.audio.newSound(Gdx.files.internal("sounds/item_pickup.wav"));
    public static final Sound soundItemChange = Gdx.audio.newSound(Gdx.files.internal("sounds/item_change.wav"));
    public static final Sound soundEnemyHit = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_hit.wav"));
    public static final Sound soundEnemyDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.wav"));
    public static final Sound soundItemBuy = Gdx.audio.newSound(Gdx.files.internal("sounds/item_buy.wav"));
    public static final Sound soundOpenChest = Gdx.audio.newSound(Gdx.files.internal("sounds/open_chest.wav"));

    private static Music currentMusic = null;

    public static void playMusic(Music music, boolean looping) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
        currentMusic = music;
        if (currentMusic != null) {
            currentMusic.setLooping(looping);
            currentMusic.setVolume(UserSettings.masterVolume * UserSettings.musicVolume);
            currentMusic.play();
        }
    }

    public static void applyVolumes() {
        if (currentMusic != null) {
            try {
                currentMusic.setVolume(UserSettings.masterVolume * UserSettings.musicVolume);
            } catch (Exception e) {
                Gdx.app.error("SoundManager", "Failed to apply music volume", e);
            }
        }
    }

    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = null;
    }

    public static void pauseMusic() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public static void resumeMusic() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    public static void playButtonClick() {
        if (soundButtonClick != null) {
            float vol = UserSettings.masterVolume * UserSettings.uiVolume;
            soundButtonClick.play(vol);
        }
    }

    public static void playPlayerHit() {
        if (soundPlayerHit != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundPlayerHit.play(vol, pitch, 0f);
        }
    }

    public static void playPlayerDeath() {
        if (soundPlayerDeath != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundPlayerDeath.play(vol, pitch, 0f);
        }
    }

    public static void playShot() {
        if (soundGunShot != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundGunShot.play(vol, pitch, 0f);
        }
    }

    public static void playReload() {
        if (soundReload != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundReload.play(vol, pitch, 0f);
        }
    }

    public static void playItemPickup() {
        if (soundItemPickup != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundItemPickup.play(vol, pitch, 0f);
        }
    }

    public static void playItemChange() {
        if (soundItemChange != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.uiVolume;
            soundItemChange.play(vol, pitch, 0f);
        }
    }

    public static void playEnemyHit() {
        if (soundEnemyHit != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundEnemyHit.play(vol, pitch, 0f);
        }
    }

    public static void playEnemyDeath() {
        if (soundEnemyDeath != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundEnemyDeath.play(vol, pitch, 0f);
        }
    }

    public static void playItemBuy() {
        if (soundItemBuy != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundItemBuy.play(vol, pitch, 0f);
        }
    }

    public static void playOpenChest() {
        if (soundOpenChest != null) {
            float pitch = 0.95f + (float) Math.random() * 0.1f;
            float vol = UserSettings.masterVolume * UserSettings.sfxVolume;
            soundOpenChest.play(vol, pitch, 0f);
        }
    }

    public static void dispose() {
        musicMenu.dispose();
        musicBoss.dispose();
        musicWin.dispose();
        musicLoose.dispose();
        soundGunShot.dispose();
        soundReload.dispose();
        soundButtonClick.dispose();
        soundPlayerHit.dispose();
        soundPlayerDeath.dispose();
        soundItemPickup.dispose();
        soundItemChange.dispose();
        soundItemBuy.dispose();
        soundOpenChest.dispose();
    }
}