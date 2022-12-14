/*
 * Copyright (c) 1996, 1997 Erich Gamma
 * All Rights Reserved
 */

package CH.ifa.draw.contrib;

import java.awt.Point;

import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.standard.ChopBoxConnector;

/**
 * A ChopPolygonConnector locates a connection point by
 * chopping the connection at the polygon boundary.
 */
public class ChopPolygonConnector extends ChopBoxConnector {

    /*
     * Serialization support.
     */
    private static final long serialVersionUID = -156024908227796826L;

    public ChopPolygonConnector() {
    }

    public ChopPolygonConnector(Figure owner) {
        super(owner);
    }

    protected Point chop(Figure target, Point from) {
        return ((PolygonFigure)target).chop(from);
    }
}

