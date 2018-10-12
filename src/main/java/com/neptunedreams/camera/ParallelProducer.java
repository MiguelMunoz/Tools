package com.neptunedreams.camera;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.stream.IntStream;
//import sun.awt.image.ByteComponentRaster;
//import sun.awt.image.BytePackedRaster;

import static java.awt.image.DataBuffer.*;

/**
 * This class is adapted from sun.awt.image.OffScreenImageSource, which implements ImageProducer. This adds parallel
 * processing to that class. It's currently tailored to Integer data.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/12/17
 * <p>Time: 2:03 PM
 *
 * @author Miguel Mu–oz
 */
class ParallelProducer implements ImageProducer {
	private static final Object lock = new Object();
	private static final int BYTE_MAX = 255;
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
		synchronized (lock) {
			consumer = ic;
		}
	}

	@Override
	public boolean isConsumer(ImageConsumer ic) {
		synchronized (lock) {
		//noinspection ObjectEquality
			return ic == consumer;
		}
	}

	@Override
	public void removeConsumer(ImageConsumer ic) {
		synchronized (lock) {
			//noinspection ObjectEquality
			if (consumer == ic) {
				consumer = null;
			}
		}
	}

	@Override
	public void startProduction(ImageConsumer ic) {
		addConsumer(ic);
		produce();
	}

	@Override
	public void requestTopDownLeftRightResend(ImageConsumer ic) {

	}

	private void produce() {
		synchronized (lock) {
			try {
				consumer.setDimensions(this.image.getWidth(), this.image.getHeight());
	//			consumer.setProperties(this.properties);
				this.sendPixels();
				consumer.imageComplete(2);
				consumer.imageComplete(3);
			} catch (NullPointerException var2) {
				if(consumer != null) {
					consumer.imageComplete(1);
				}
			}
		}

	}

	private void sendPixels() {
		ColorModel colorModel = this.image.getColorModel();
		WritableRaster raster = this.image.getRaster();
		int numDataElements = raster.getNumDataElements();
		final int arraySize = this.width * numDataElements;
//		System.out.printf("Width %d * %d = %d (arraySize)%n", this.width, numDataElements, arraySize);
		final int[] intArray = new int[arraySize];
		int dataType = raster.getDataBuffer().getDataType();
		boolean var6 = true;
		byte[] bytes;
		int yy;
		int xx;
		if(colorModel instanceof IndexColorModel) {
			throw new ModelNotSupportedException(IndexColorModel.class);
//			bytes = new byte[this.width];
//			synchronized (lock) {
//				consumer.setColorModel(colorModel);
//				if(raster instanceof ByteComponentRaster) {
//					var6 = false;
//	
//					for(yy = 0; yy < this.height; ++yy) {
//						raster.getDataElements(0, yy, this.width, 1, bytes);
//						consumer.setPixels(0, yy, this.width, 1, colorModel, bytes, 0, this.width);
//					}
//				} else if(raster instanceof BytePackedRaster) {
//					var6 = false;
//	
//					for(yy = 0; yy < this.height; ++yy) {
//						raster.getPixels(0, yy, this.width, 1, intArray);
//	
//						for(xx = 0; xx < this.width; ++xx) {
//							bytes[xx] = (byte)intArray[xx];
//						}
//	
//						consumer.setPixels(0, yy, this.width, 1, colorModel, bytes, 0, this.width);
//					}
//				} else if(dataType == TYPE_SHORT || dataType == TYPE_INT) {
//					var6 = false;
//	
//					for(yy = 0; yy < this.height; ++yy) {
//						raster.getPixels(0, yy, this.width, 1, intArray);
//						consumer.setPixels(0, yy, this.width, 1, colorModel, intArray, 0, this.width);
//					}
//				}
//			}
		} else if(colorModel instanceof DirectColorModel) {
			synchronized (lock) {
				consumer.setColorModel(colorModel);
				var6 = false;
				label93:
				switch(dataType) {
					case TYPE_BYTE:
						bytes = new byte[this.width];
						yy = 0;
	
						while(true) {
							if(yy >= this.height) {
								break label93;
							}
	
							raster.getDataElements(0, yy, this.width, 1, bytes);
	
							for(xx = 0; xx < this.width; ++xx) {
								intArray[xx] = bytes[xx] & BYTE_MAX; // 255
							}
	
							consumer.setPixels(0, yy, this.width, 1, colorModel, intArray, 0, this.width);
							++yy;
						}
					case TYPE_USHORT:
						short[] shorts = new short[this.width];
						yy = 0;
	
						while(true) {
							if(yy >= this.height) {
								break label93;
							}
	
							raster.getDataElements(0, yy, this.width, 1, shorts);
	
							for(int i = 0; i < this.width; ++i) {
	//							intArray[i] = shorts[i] & '\uffff';
								intArray[i] = shorts[i];
							}
	
							consumer.setPixels(0, yy, this.width, 1, colorModel, intArray, 0, this.width);
							++yy;
						}
					case TYPE_SHORT:
					default:
						var6 = true;
						break;
					case TYPE_INT:
	//					RowInLineProcessor processor = new RowInLineProcessor(colorModel, raster, intArray);
	//					Spliterator<Integer> spliterator = Spliterators.spliterator(new SequentialIterator(this.height), this.height,
	//							Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.NONNULL);
	//					StreamSupport.stream(spliterator, true)
	////							.parallel()
	//							.map(processor::process)
	//							.count();
						// This loop is what gets done by the stream:
						for(int h = 0; h < this.height; ++h) {
							processHeightRow(colorModel, raster, intArray, h);
						}
				}
			}
		}

		if(var6) {
			ColorModel rgbDefault = ColorModel.getRGBdefault();
			RowPixelProcessor rowProcessor = new RowPixelProcessor(rgbDefault, arraySize);
			consumer.setColorModel(rgbDefault);
			IntStream intStream = IntStream.range(0, this.height);

//			Spliterator<Integer> spliterator = SequentialIterator.sequentialSpliterator(this.height);
//					StreamSupport.stream(spliterator, true)
			intStream.parallel()
						.map((rowProcessor::process))
						.count();

//			for(y = 0; y < this.height; ++y) {
//				for(x = 0; x < this.width; ++x) {
//					intArray[x] = this.image.getRGB(x, y);
//				}
//
//				consumer.setPixels(0, y, this.width, 1, rgbDefault, intArray, 0, this.width);
//			}
		}

	}

	private void processHeightRow(ColorModel pColorModel, WritableRaster pRaster, int[] pIntArray, int pH) {
		pRaster.getDataElements(0, pH, this.width, 1, pIntArray);
		synchronized (lock) {
			consumer.setPixels(0, pH, this.width, 1, pColorModel, pIntArray, 0, this.width);
		}
	}

	/**
	 * This class processes each pixel separately
	 */
	private class RowPixelProcessor {
		private final ColorModel colorModel;
//		private final int[] intArray;
		private final int size;
		
		RowPixelProcessor(ColorModel model, int arraySize) {
			colorModel = model;
			size = arraySize;
		}
		
		public int process(int y) {
			int[] intArray = new int[size];
			for(int x = 0; x < width; ++x) {
				intArray[x] = image.getRGB(x, y);
			}
			
//			synchronized (lock) {
				consumer.setPixels(0, y, width, 1, colorModel, intArray, 0, width);
//			}
				return 0;
		}
	}

//	/**
//	 * This class processes the rows an entire line at a time.
//	 */
//	private class RowInLineProcessor {
//		final ColorModel model;
//		final WritableRaster raster;
//		final int[] intArray;
//		RowInLineProcessor(ColorModel pModel, WritableRaster pRaster, int[] array) {
//			model = pModel;
//			raster = pRaster;
//			intArray = array;
//		}
//		
//		public int process(int h) {
//			Thread thread = Thread.currentThread();
//			System.out.printf("Thread: id: %d (%s)%n", thread.getId(), thread.getName());
//			raster.getDataElements(0, h, width, 1, intArray);
//			synchronized (lock) {
//				consumer.setPixels(0, h, width, 1, model, intArray, 0, width);
//			}
//			return 0;
//		}
//
//		RowInLineProcessor(BufferedImage image) {
//			model = image.getColorModel();
//			raster = image.getRaster();
//			int numDataElements = raster.getNumDataElements();
//			intArray = new int[width * numDataElements];
//		}
//	}

	public static class ModelNotSupportedException extends RuntimeException {
		private final Class<?> modelClass;
		public ModelNotSupportedException(Class<? extends ColorModel> model) {
			modelClass = model;
		}

		@Override
		public String getMessage() {
			return "Model Not Supported: " + modelClass.getName();
		}
	}
	
}
