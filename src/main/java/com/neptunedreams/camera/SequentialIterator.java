package com.neptunedreams.camera;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;

import static java.util.Spliterator.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 9/14/17
 * <p>Time: 12:18 AM
 *
 * @author Miguel Mu–oz
 */
public class SequentialIterator implements Iterator<Integer> {
	private static final int CHARACTERISTICS = CONCURRENT | DISTINCT | NONNULL | IMMUTABLE;
	private final int max;
	private int index = 0;
	
	private SequentialIterator(int count) {
		max = count;
	}
	
	private SequentialIterator(int start, int count) {
		index = start;
		max = start + count;
	}

	@Override
	public boolean hasNext() {
		return index < max;
	}

	@Override
	public Integer next() {
//		Thread t = Thread.currentThread();
//		System.out.printf("Incrementing %d on Thread id %d (%s)%n", index, t.getId(), t.getName());
		if (!hasNext()) {
			throw new NoSuchElementException("Max: " + max);
		}
		return index++;
	}

	/**
	 * Return a {@literal Spliterator<Integer>} that returns a sequence of integers, starting with zero and ending with count-1. So setting 
	 * count to 100 will give you a spliterator with values from 0 to 99.
	 * @param count The maximum value, exclusinve
	 * @return A spliterator going from 0 to count-1
	 */
	public static Spliterator<Integer> sequentialSpliterator(int count) {
		return Spliterators.spliterator(new SequentialIterator(count), count, CHARACTERISTICS);
	}

	/**
	 * Return a {@literal Spliterator<Integer>} that returns {@code count} values, starting with {@code start}.
	 * @param start The initial value.
	 * @param count The total number of values
	 * @return A Spliterator that starts with {@code start} and delivers {@code count} values
	 */
	public static Spliterator<Integer> sizedSpliterator(int start, int count) {
		return Spliterators.spliterator(new SequentialIterator(start, count), count, CHARACTERISTICS);
	}

	/**
	 * Retur a {@literal Spliterator<Integer>} that returns a range of integer values, starting with 
	 * {@code startInclusive} and ending with {@code endExclusive}. So calling {@code rangedSpliterator(100, 200)} will
	 * deliver values from 100 to 199.
	 * @param startInclusive The lowest value of the integers to deliver
	 * @param endExclusive One more than the highest value to deliver.
	 * @return A Spliterator that delivers values in the specified range.
	 */
	public static Spliterator<Integer> rangedSpliterator(int startInclusive, int endExclusive) {
		int count = endExclusive - startInclusive;
		return Spliterators.spliterator(new SequentialIterator(startInclusive, count), count, CHARACTERISTICS);
	}
}
/*
    11  12  13  14  15  16  17
 m   1   2   3   4   5   6   7   
22  66  32  91  16  57   7  72
23  67  33  92  17  58   8  73
24  68  34  93  18  59   9  74
25	63  35  88  13  60  10  75
19	64  36  89  14  61  11  69
20	65  37  90  15  62  12  70
21	54  29  97  44  82   4  71
41	55  30  98  45  83   5
42	56  31  99  46  84   6
43	51  26 100  47  85
49	52  27  79  48  86
50	53  28  80      87
		94	76	81       1
		95  77  38       2
		96  78  39       3
						40
						
mn: (12)                19-25             41-43       49-50
11: (15)                                                    51-56       63-68                               94-96
12: (15)                      26-37                                                 76-78
13: (16)                            38-40                                                 79-81       88-93       97-100
14: (11)          13-18                         44-48
15: (15) 1-3                                                      57-62                         82-87
16: ( 9)     4-12
17: ( 7)                                                                      69-75
 */
