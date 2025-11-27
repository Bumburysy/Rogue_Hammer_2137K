package project.roguelike.levels;

import project.roguelike.levels.RoomData.RoomType;

public class Layout1 {
  public static final RoomData[][] layout = {
      { new RoomData(RoomType.START, null), null, new RoomData(RoomType.END, null) },
      { new RoomData(RoomType.NORMAL, null), new RoomData(RoomType.TRAP, null), new RoomData(RoomType.BOSS, null) },
      { new RoomData(RoomType.SHOP, null), null, new RoomData(RoomType.CHEST, null) }
  };
};
