package project.roguelike.levels;

import project.roguelike.levels.RoomData.RoomType;

public class Layout4 {
        public static final RoomData[][] layout = {
                        { null, new RoomData(RoomType.CHEST, null), new RoomData(RoomType.NORMAL, null),
                                        new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.START, null) },
                        { new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.NORMAL, null),
                                        new RoomData(RoomType.SHOP, null), null, null },
                        { null, new RoomData(RoomType.TRAP, null), new RoomData(RoomType.NORMAL, null),
                                        new RoomData(RoomType.BOSS, null), new RoomData(RoomType.CHEST, null) },
                        { null, null, new RoomData(RoomType.TRAP, null), null, null },
                        { new RoomData(RoomType.END, null), new RoomData(RoomType.NORMAL, null),
                                        new RoomData(RoomType.NORMAL, null), null, null }
        };
}