package project.roguelike.levels;

import project.roguelike.levels.RoomData.RoomType;

public class Layout2 {
        public static final RoomData[][] layout = {
                        { null, new RoomData(RoomType.START, null), null, new RoomData(RoomType.NORMAL, null) },
                        { new RoomData(RoomType.BOSS, null), new RoomData(RoomType.NORMAL, null), null,
                                        new RoomData(RoomType.NORMAL, null) },
                        { new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.TRAP, null),
                                        new RoomData(RoomType.NORMAL, null),
                                        new RoomData(RoomType.NORMAL, null) },
                        { null, new RoomData(RoomType.NORMAL, null), null, null }
        };
};
