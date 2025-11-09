package project.roguelike.levels;

import project.roguelike.rooms.Room;

public class RoomData {
    public RoomType type;
    public Room.RoomShape shape;

    public enum RoomType {
        NORMAL, START, END, BOSS, SHOP, TRAP, CHEST
    }

    public RoomData(RoomType type, Room.RoomShape shape) {
        this.type = type;
        this.shape = shape;
    }
}
