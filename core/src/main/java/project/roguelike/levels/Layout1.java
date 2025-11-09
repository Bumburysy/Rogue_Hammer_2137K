package project.roguelike.levels;

import project.roguelike.rooms.Room.RoomShape;
import project.roguelike.levels.RoomData.RoomType;

public class Layout1 {

  public static final RoomData[][] layout = {
      { null, new RoomData(RoomType.START, RoomShape.D_SHAPE_S), null },
      { new RoomData(RoomType.NORMAL, RoomShape.L_SHAPE_E), new RoomData(RoomType.NORMAL, RoomShape.T_SHAPE_N),
          new RoomData(RoomType.NORMAL, RoomShape.L_SHAPE_S) },
      { new RoomData(RoomType.NORMAL, RoomShape.L_SHAPE_N), new RoomData(RoomType.NORMAL, RoomShape.I_SHAPE_E),
          new RoomData(RoomType.NORMAL, RoomShape.L_SHAPE_W) }
  };
};
