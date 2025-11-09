package project.roguelike.rooms;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import project.roguelike.core.GameConfig;

public class Room {
    private Texture floor, wallTop, wallRight;
    private Texture doorUp, doorRight;
    private TextureRegion floorRegion, wallTopRegion, wallBottomRegion, wallLeftRegion, wallRightRegion, doorUpRegion,
            doorDownRegion, doorLeftRegion, doorRightRegion;
    private Vector2 position;
    private RoomShape shape;
    private List<DoorDirection> doors;
    private int gridRow = -1;
    private int gridCol = -1;
    private float wallThickness = GameConfig.WALL_THICKNESS;
    private float roomWidth = GameConfig.ROOM_WIDTH;
    private float roomHeight = GameConfig.ROOM_HEIGHT;
    private float doorWidth = wallThickness * 2f;
    private float tileSize = GameConfig.TILE_SIZE;

    public enum RoomShape {
        O_SHAPE, I_SHAPE_N, I_SHAPE_E, T_SHAPE_N, T_SHAPE_E, T_SHAPE_S, T_SHAPE_W, L_SHAPE_N, L_SHAPE_E, L_SHAPE_S,
        L_SHAPE_W, D_SHAPE_N, D_SHAPE_E, D_SHAPE_S, D_SHAPE_W
    }

    public enum DoorDirection {
        UP, DOWN, LEFT, RIGHT
    }

    public Room(Vector2 position, RoomShape shape) {
        this.position = position;
        this.shape = shape;

        this.floor = new Texture("textures/floor.png");
        this.wallTop = new Texture("textures/wall_top.png");
        this.wallRight = new Texture("textures/wall_right.png");
        this.doorUp = new Texture("textures/door_up.png");
        this.doorRight = new Texture("textures/door_right.png");

        this.floor.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.wallTop.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.wallRight.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.doorUp.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.doorRight.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        this.floorRegion = new TextureRegion(floor);
        this.wallTopRegion = new TextureRegion(wallTop);
        this.wallBottomRegion = new TextureRegion(wallTop);
        this.wallLeftRegion = new TextureRegion(wallRight);
        this.wallRightRegion = new TextureRegion(wallRight);
        this.doorUpRegion = new TextureRegion(doorUp);
        this.doorDownRegion = new TextureRegion(doorUp);
        this.doorLeftRegion = new TextureRegion(doorRight);
        this.doorRightRegion = new TextureRegion(doorRight);

        addDoors();
        setRegions();

        wallBottomRegion.flip(true, true);
        wallLeftRegion.flip(true, true);
        doorDownRegion.flip(true, true);
        doorLeftRegion.flip(true, true);
    }

    private void addDoors() {
        doors = new ArrayList<>();
        switch (shape) {
            case O_SHAPE: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.DOWN);
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
                break;
            }
            case I_SHAPE_E: {
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
                break;
            }
            case I_SHAPE_N: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case T_SHAPE_N: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
                break;
            }
            case T_SHAPE_E: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.RIGHT);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case T_SHAPE_S: {
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.RIGHT);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case T_SHAPE_W: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case L_SHAPE_N: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.RIGHT);
                break;
            }
            case L_SHAPE_E: {
                doors.add(DoorDirection.RIGHT);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case L_SHAPE_S: {
                doors.add(DoorDirection.LEFT);
                doors.add(DoorDirection.DOWN);
                break;
            }
            case L_SHAPE_W: {
                doors.add(DoorDirection.UP);
                doors.add(DoorDirection.LEFT);
                break;
            }
            case D_SHAPE_N: {
                doors.add(DoorDirection.UP);
                break;
            }
            case D_SHAPE_E: {
                doors.add(DoorDirection.RIGHT);
                break;
            }
            case D_SHAPE_S: {
                doors.add(DoorDirection.DOWN);
                break;
            }
            case D_SHAPE_W: {
                doors.add(DoorDirection.LEFT);
                break;
            }
        }
    }

    private void setRegions() {
        float floorScaleX = roomWidth / tileSize;
        float floorScaleY = roomHeight / tileSize;
        float wallScaleX = roomWidth / tileSize;
        float wallScaleY = wallThickness / tileSize;
        float doorWidth = wallThickness * 2f;
        float doorScaleX = doorWidth / tileSize;
        float doorScaleY = wallThickness / tileSize;

        floorRegion.setRegion(0, 0, (int) (16 * floorScaleX), (int) (16 * floorScaleY));
        wallTopRegion.setRegion(0, 0, (int) (16 * wallScaleX), (int) (16 * wallScaleY));
        wallBottomRegion.setRegion(0, 0, (int) (16 * wallScaleX), (int) (16 * wallScaleY));
        wallLeftRegion.setRegion(0, 0, (int) (16 * wallScaleY), (int) (16 * floorScaleY));
        wallRightRegion.setRegion(0, 0, (int) (16 * wallScaleY), (int) (16 * floorScaleY));
        doorUpRegion.setRegion(0, 0, (int) (16 * doorScaleX), (int) (16 * doorScaleY));
        doorDownRegion.setRegion(0, 0, (int) (16 * doorScaleX), (int) (16 * doorScaleY));
        doorLeftRegion.setRegion(0, 0, (int) (16 * doorScaleY), (int) (16 * doorScaleX));
        doorRightRegion.setRegion(0, 0, (int) (16 * doorScaleY), (int) (16 * doorScaleX));
    }

    public void render(SpriteBatch batch) {
        batch.draw(floorRegion, position.x, position.y, roomWidth, roomHeight);
        batch.draw(wallTopRegion,
                position.x,
                position.y + roomHeight,
                roomWidth,
                wallThickness);
        batch.draw(wallBottomRegion,
                position.x,
                position.y - wallThickness,
                roomWidth,
                wallThickness);
        batch.draw(wallLeftRegion,
                position.x - wallThickness / 2,
                position.y - wallThickness,
                wallThickness / 2,
                roomHeight + wallThickness * 2);
        batch.draw(wallRightRegion,
                position.x + roomWidth,
                position.y - wallThickness,
                wallThickness / 2,
                roomHeight + wallThickness * 2);
        for (DoorDirection door : doors) {
            switch (door) {
                case UP: {
                    batch.draw(doorUpRegion,
                            position.x + roomWidth / 2f - doorWidth / 2f,
                            position.y + roomHeight,
                            doorWidth,
                            wallThickness);
                    break;
                }
                case DOWN: {
                    batch.draw(doorDownRegion,
                            position.x + roomWidth / 2f - doorWidth / 2f,
                            position.y - wallThickness,
                            doorWidth,
                            wallThickness);
                    break;
                }
                case LEFT: {
                    batch.draw(doorLeftRegion,
                            position.x - wallThickness / 2f,
                            position.y + roomHeight / 2f - doorWidth / 2f,
                            wallThickness / 2f,
                            doorWidth);
                    break;
                }
                case RIGHT: {
                    batch.draw(doorRightRegion,
                            position.x + roomWidth,
                            position.y + roomHeight / 2f - doorWidth / 2f,
                            wallThickness / 2f,
                            doorWidth);
                    break;
                }
            }
        }
    }

    public void dispose() {
        floor.dispose();
        wallTop.dispose();
        wallRight.dispose();
        doorUp.dispose();
        doorRight.dispose();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getCenter() {
        return new Vector2(position.x + GameConfig.ROOM_WIDTH / 2f, position.y + GameConfig.ROOM_HEIGHT / 2f);
    }

    public void setGridPosition(int row, int col) {
        this.gridRow = row;
        this.gridCol = col;
    }

    public int getGridRow() {
        return gridRow;
    }

    public int getGridCol() {
        return gridCol;
    }

    public List<DoorDirection> getDoors() {
        return doors;
    }

    public Rectangle getDoorBounds(DoorDirection direction) {
        float doorSize = wallThickness * 1.5f;

        switch (direction) {
            case UP: {
                return new Rectangle(
                        position.x + roomWidth / 2f - doorSize / 2f,
                        position.y + roomHeight - doorSize / 2f,
                        doorSize,
                        doorSize);
            }
            case DOWN: {
                return new Rectangle(
                        position.x + roomWidth / 2f - doorSize / 2f,
                        position.y - doorSize / 2f,
                        doorSize,
                        doorSize);
            }
            case LEFT: {
                return new Rectangle(
                        position.x - doorSize / 2f,
                        position.y + roomHeight / 2f - doorSize / 2f,
                        doorSize,
                        doorSize);
            }
            case RIGHT: {
                return new Rectangle(
                        position.x + roomWidth - doorSize / 2f,
                        position.y + roomHeight / 2f - doorSize / 2f,
                        doorSize,
                        doorSize);
            }
        }
        return null;
    }
}
