package project.roguelike.levels;

import project.roguelike.levels.RoomData.RoomType;

public class Layout5 {
    public static final RoomData[][] layout = {
            { new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.NORMAL, null),
                    new RoomData(RoomType.CHEST, null), new RoomData(RoomType.SHOP, null) },
            { new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.TRAP, null),
                    new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.NORMAL, null) },
            { new RoomData(RoomType.TRAP, null), new RoomData(RoomType.START, null),
                    new RoomData(RoomType.CHEST, null), new RoomData(RoomType.BOSS, null) },
            { new RoomData(RoomType.CHEST, null), new RoomData(RoomType.NORMAL, null),
                    new RoomData(RoomType.BOSS, null), new RoomData(RoomType.END, null) }
    };
}