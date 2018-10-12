package com.neptunedreams.functions;

import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.neptunedreams.functions.FunctionUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/4/18
 * <p>Time: 11:45 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"WeakerAccess", "HardCodedStringLiteral", "ConstantConditions"})
public class FunctionUtilityTest {
	@Test
	public void test() {
		USB usb = new USB();
		SoundCard soundCard = new SoundCard(usb);
		Computer computer = new Computer(soundCard);

		final String soundCardVersionOpt = getSoundCardVersionOpt(Optional.of(computer));
		assertEquals("1.0", soundCardVersionOpt);
		final String soundCardVersion = getSoundCardVersion(computer);
		assertEquals("1.0", soundCardVersion);

		SoundCard soundCardNoUSB = new SoundCard(null);
		Computer noUsbComputer = new Computer(soundCardNoUSB);

		final String soundCardVersionNoUsbOpt = getSoundCardVersionOpt(Optional.of(noUsbComputer));
		assertEquals("UNKNOWN", soundCardVersionNoUsbOpt);
		final String soundCardVersionNoUsb = getSoundCardVersion(noUsbComputer);
		assertEquals("NOT KNOWN", soundCardVersionNoUsb);

		Computer noSoundCard = new Computer(null);

		final String soundCardVersionNoSoundCardOpt = getSoundCardVersionOpt(Optional.of(noSoundCard));
		assertEquals("UNKNOWN", soundCardVersionNoSoundCardOpt);
		final String soundCardVersionNoSoundCard = getSoundCardVersion(noSoundCard);
		assertEquals("NOT KNOWN", soundCardVersionNoSoundCard);

		final Optional<Computer> noComputerOpt = Optional.empty();

		final String soundCardVersionNoComputerOpt = getSoundCardVersionOpt(noComputerOpt);
		assertEquals("UNKNOWN", soundCardVersionNoComputerOpt);
		final String soundCardVersionNoComputer = getSoundCardVersion(null);
		assertEquals("NOT KNOWN", soundCardVersionNoComputer);

	}

	public static class Computer {
		private SoundCard soundCard;

		public Computer(SoundCard soundCard) { this.soundCard = soundCard;}

		public Optional<SoundCard> getSoundCardOpt() { return Optional.ofNullable(soundCard); }
		public SoundCard getSoundCard() { return soundCard; }
	}

	public static class SoundCard {
		private USB usb;

		public SoundCard(USB usb) {
			this.usb = usb;
		}

		public Optional<USB> getUsbOpt() { return Optional.ofNullable(usb); }
		public USB getUsb() { return usb; }
	}

	public static class USB {
		public USB() { }
		public String getVersion() { return "1.0"; }
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static String getSoundCardVersionOpt(Optional<Computer> computer) {
		Optional<Integer> i = Optional.of(5);
		
		return computer
				.flatMap(Computer::getSoundCardOpt)
				.flatMap(SoundCard::getUsbOpt)
				.map(USB::getVersion)
				.orElse("UNKNOWN");
	}

	public static String getSoundCardVersion(Computer computer) {
		return opt(computer)
				.flatMap(optFun(Computer::getSoundCard))
				.flatMap(optFun(SoundCard::getUsb))
				.map(USB::getVersion)
				.orElse("NOT KNOWN");
	}
}
