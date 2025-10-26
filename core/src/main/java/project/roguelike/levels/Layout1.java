package project.roguelike.levels;

import project.roguelike.rooms.Room.RoomShape;
import project.roguelike.rooms.Room.RoomType;
import project.roguelike.rooms.RoomData;

public class Layout1 {

  public static final RoomData[][] layout = {
      { null, new RoomData(RoomType.NORMAL, RoomShape.I_SHAPE_V), null },
      { new RoomData(RoomType.NORMAL, RoomShape.I_SHAPE_H), new RoomData(RoomType.NORMAL, RoomShape.O_SHAPE),
          new RoomData(RoomType.NORMAL, RoomShape.I_SHAPE_H) },
      { null, new RoomData(RoomType.NORMAL, RoomShape.I_SHAPE_V), null }
  };
};
