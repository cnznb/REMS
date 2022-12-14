/*
 * @(#)Layouter.java 5.2
 *
 */
package CH.ifa.draw.contrib;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;

import CH.ifa.draw.util.Storable;

/**
 * A Layouter encapsulates a algorithm to layout
 * a figure. It is passed on to a figure which delegates the 
 * layout task to the Layouter's layout method. 
 * The Layouter might need access to some information 
 * specific to a certain figure in order to layout it out properly.
 *
 * Note: Currently, only the GraphicalCompositeFigure uses
 *       such a Layouter to layout its child components.
 *
 * @see		GraphicalCompositeFigure
 * @author	Wolfram Kaiser
 */
public interface Layouter extends Serializable, Storable {

	/*
	 * Calculate the layout for the figure and all its subelements. The
	 * layout is not actually performed but just its dimensions are calculated.
	 *
	 * @param origin start point for the layout
	 * @param corner minimum corner point for the layout
	 */	
	public Rectangle calculateLayout(Point origin, Point corner);

	/**
	 * Method which lays out a figure. It is called by the figure
	 * if a layout task is to be performed. Implementing classes
	 * specify a certain layout algorithm in this method.
	 *
	 * @param origin start point for the layout
	 * @param corner minimum corner point for the layout
	 */	
	public Rectangle layout(Point origin, Point corner);

	/**
	 * Set the insets for spacing between the figure and its subfigures
	 *
	 * @param newInsets new spacing dimensions
	 */
	public void setInsets(Insets newInsets);

	/**
	 * Get the insets for spacing between the figure and its subfigures
	 *
	 * @return spacing dimensions
	 */	
	public Insets getInsets();
}