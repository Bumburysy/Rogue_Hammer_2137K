package project.roguelike.rooms;

public class RoomData {
    public Room.RoomType type;
    public Room.RoomShape shape;

    public RoomData(Room.RoomType type, Room.RoomShape shape) {
        this.type = type;
        this.shape = shape;
    }
}
