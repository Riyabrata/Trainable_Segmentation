/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2010 - 2022 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
/**
 * Trainable_Segmentation plug-in for ImageJ and Fiji.
 * 2010 Ignacio Arganda-Carreras 
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation (http://www.gnu.org/licenses/gpl.txt )
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package trainableSegmentation;

import fiji.util.gui.OverlayedImageCanvas.Overlay;
import ij.process.ImageProcessor;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
/**
 * This class implements an overlay based on an image.
 * The overlay paints the image with a specific composite mode.
 *  
 * @author Ignacio Arganda-Carreras
 *
 */
public class ImageOverlay implements Overlay{

	private ImageProcessor imp = null;
	private Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	
	/**
	 * Empty constructor.
	 */
	public ImageOverlay(){}
	/**
	 * Construct an image overlay.
	 * @param imp image to be used
	 */
	public ImageOverlay(ImageProcessor imp){
		this.imp = imp;
	}
	
	//@Override
	public void paint(Graphics g, int x, int y, double magnification) {
		if ( null == this.imp )
			return;
						
		Graphics2D g2d = (Graphics2D)g;						
				
		final AffineTransform originalTransform = g2d.getTransform();
		final AffineTransform at = new AffineTransform();
		at.scale( magnification, magnification );
		at.translate( - x, - y );
		at.concatenate( originalTransform );
		
		g2d.setTransform( at );
		
				
		final Composite originalComposite = g2d.getComposite();
		g2d.setComposite(composite);
		g2d.drawImage(imp.getBufferedImage(), null, null);	
		
		g2d.setTransform( originalTransform );
		g2d.setComposite(originalComposite);
	}
	
	/**
	 * Set composite mode
	 * 
	 * @param composite composite mode
	 */
	public void setComposite (Composite composite)
	{this.composite = composite;}
	
	/**
	 * Set image processor to be painted in the overlay
	 * 
	 * @param imp input image
	 */
	public void setImage ( ImageProcessor imp)
	{this.imp = imp;}

}
