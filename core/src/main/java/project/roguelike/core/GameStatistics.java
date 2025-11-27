package project.roguelike.core;

public class GameStatistics {
    private int enemiesKilled = 0;
    private int roomsCleared = 0;
    private int damageDealt = 0;
    private int damageTaken = 0;
    private int bulletsFired = 0;
    private int bulletsHit = 0;
    private float gameTime = 0f;

    private int currentLevel = 0;
    private int levelsCompleted = 0;

    public void update(float delta) {
        gameTime += delta;
    }

    public void onEnemyKilled() {
        enemiesKilled++;
    }

    public void onDamageDealt(int damage) {
        if (damage <= 0)
            return;
        damageDealt += damage;
    }

    public void onDamageTaken(int damage) {
        if (damage <= 0)
            return;
        damageTaken += damage;
    }

    public void onRoomCleared() {
        roomsCleared++;
    }

    public void onBulletFired() {
        bulletsFired++;
    }

    public void onBulletHit() {
        bulletsHit++;
    }

    public float getAccuracy() {
        return bulletsFired > 0 ? (float) bulletsHit / bulletsFired * 100f : 0f;
    }

    public void reset() {
        enemiesKilled = 0;
        roomsCleared = 0;
        damageDealt = 0;
        damageTaken = 0;
        bulletsFired = 0;
        bulletsHit = 0;
        gameTime = 0f;
        currentLevel = 0;
        levelsCompleted = 0;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public int getRoomsCleared() {
        return roomsCleared;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public int getDamageTaken() {
        return damageTaken;
    }

    public int getBulletsFired() {
        return bulletsFired;
    }

    public int getBulletsHit() {
        return bulletsHit;
    }

    public float getGameTime() {
        return gameTime;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(0, level);
    }

    public void incrementCurrentLevel() {
        this.currentLevel = Math.max(0, this.currentLevel) + 1;
    }

    public int getLevelsCompleted() {
        return levelsCompleted;
    }

    public void incrementLevelsCompleted() {
        this.levelsCompleted++;
    }
}