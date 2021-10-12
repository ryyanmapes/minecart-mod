package com.alc.moreminecarts.misc;

import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;

public class RailUtil {

    public static RailShape FacingToShape(Direction direction, boolean ascending) {
        switch (direction) {
            case NORTH:
                return ascending? RailShape.ASCENDING_NORTH : RailShape.NORTH_SOUTH;
            case EAST:
                return ascending? RailShape.ASCENDING_EAST : RailShape.EAST_WEST;
            case SOUTH:
                return ascending? RailShape.ASCENDING_SOUTH : RailShape.NORTH_SOUTH;
            case WEST:
                return ascending? RailShape.ASCENDING_WEST : RailShape.EAST_WEST;
        }
        // TODO error here
        return RailShape.NORTH_SOUTH;
    }

}
