package project.roguelike;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import project.roguelike.entities.Player;
import project.roguelike.entities.Enemy;
import project.roguelike.entities.Bullet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.GL20;
import project.roguelike.PlayerEnemyTest;

public class PlayerEnemyTest implements Scene {

    private SpriteBatch batch;
    private Player player;
    private List<Enemy> enemies;

    @Override
    public void create() {
        batch = new SpriteBatch();
        player = new Player(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() /
                2f);

        enemies = new ArrayList<>();
        enemies.add(new Enemy(100, 500));
        enemies.add(new Enemy(600, 100));

    }

    @Override
    public void render(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();

        player.update(delta);

        for (Enemy e : enemies) {
            e.update(delta, player.getPosition(), player.getBounds(), player);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy e = enemyIter.next();

            Iterator<Bullet> bulletIter = player.getBullets().iterator();
            while (bulletIter.hasNext()) {
                Bullet b = bulletIter.next();
                if (b.getBounds().overlaps(e.getBounds())) {
                    e.takeDamage(1);
                    bulletIter.remove();
                    b.dispose();
                    break;
                }
            }

            if (e.isDead()) {
                enemyIter.remove();
            }
        }

        float stopDistance = 32 * 0.75f;
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e1 = enemies.get(i);
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy e2 = enemies.get(j);

                Vector2 toOther = new Vector2(e2.getPosition()).sub(e1.getPosition());
                float distance = toOther.len();
                if (distance < stopDistance && distance > 0) {
                    Vector2 direction = toOther.nor();
                    float penetration = stopDistance - distance;
                    e1.getPosition().sub(direction.scl(penetration * 0.5f));
                    e2.getPosition().add(direction.scl(penetration * 0.5f));
                    e1.getBounds().setPosition(e1.getPosition());
                    e2.getBounds().setPosition(e2.getPosition());
                }
            }
        }
        player.render(batch);
        for (Enemy e : enemies)
            e.render(batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        for (Enemy e : enemies)
            e.dispose();
    }
}
