package com.neptunedreams.camera;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.WritableRaster;
import java.util.stream.IntStream;

//import sun.awt.image.ByteComponentRaster;
//import sun.awt.image.BytePackedRaster;

/**
 * This class is adapted from sun.awt.image.OffScreenImageSource, which implements ImageProducer. This adds parallel
 * processing to that class. It's currently tailored to Integer data. The original was written to be Thread-safe, but
 * that doesn't seem to be necessary for this application. The lock and all the synchronization has been removed.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/12/17
 * <p>Time: 2:03 PM
 *
 * @author Miguel Mu\u00f1oz
 */
class ParallelProducer implements ImageProducer {
	private ImageConsumer consumer; // The consumer is the image filter.
	private final BufferedImage image;
	private final int width;
	private final int height;
	
	ParallelProducer(BufferedImage theImage) {
		image = theImage;
		width = image.getWidth();
		height = image.getHeight();
	}

	@Override
	public void addConsumer(ImageConsumer ic) {
			consumer = ic;
	}

	@Override
	public boolean isConsumer(ImageConsumer ic) {
		//noinspection ObjectEquality
			return ic == consumer;
	}

	@Override
	public void removeConsumer(ImageConsumer ic) {
			//noinspection ObjectEquality
			if (consumer == ic) {
				//noinspection ConstantConditions
				consumer = null;
			}
	}

	@Override
	public void startProduction(ImageConsumer ic) {
		addConsumer(ic);
		produce();
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer ic) {
		Thread.dumpStack();

	}

	private void produce() {
		// Yeah, this is really bad coding. But I copied this code from one of Sun's classes, so I'm not going to 
		// rewrite it, since I'm not sure where the exception might get thrown.
		//noinspection ProhibitedExceptionCaught
		try {
				consumer.setDimensions(this.image.getWidth(), this.image.getHeight());
				this.sendPixels();
				consumer.imageComplete(2);
				consumer.imageComplete(3);
			} catch (NullPointerException npe) {
				if(consumer != null) {
					consumer.imageComplete(1);
				}
			}
	}

	private void sendPixels() {
		WritableRaster raster = this.image.getRaster();
		int numDataElements = raster.getNumDataElements();
		final int arraySize = this.width * numDataElements;

		ColorModel rgbDefault = ColorModel.getRGBdefault();
		RowPixelProcessor rowProcessor = new RowPixelProcessor(rgbDefault, arraySize);
		consumer.setColorModel(rgbDefault);

		IntStream.range(0, this.height)
				.parallel()
				.forEach(rowProcessor::process);
	}

	/**
	 * This class processes each pixel separately
	 */
	private class RowPixelProcessor {
		private final ColorModel colorModel;
		private final int size;
		
		RowPixelProcessor(ColorModel model, int arraySize) {
			colorModel = model;
			size = arraySize;
		}
		
		void process(int y) {
			int[] intArray = new int[size];
			for(int x = 0; x < width; ++x) {
				intArray[x] = image.getRGB(x, y);
			}
			
				consumer.setPixels(0, y, width, 1, colorModel, intArray, 0, width);
		}
	}
}
