package me.mastercapexd.guiitemgenerator.util;

import java.util.Random;

public class BoundableRandom {

	private Random random = new Random();

	public byte nextByte(byte min, byte max) {
		byte randomByte = (byte) random.nextInt(max);
		if (randomByte < min)
			randomByte = min;
		return randomByte;
	}

	public short nextShort(short min, short max) {
		short randomShort = (short) random.nextInt(max);
		if (randomShort < min)
			randomShort = min;
		return randomShort;
	}

	public int nextInt(int min, int max) {
		int randomInt = random.nextInt(max);
		if (randomInt < min)
			randomInt = min;
		return randomInt;
	}

	public long nextLong(long min, long max) {
		long randomLong = random.nextLong();
		if (randomLong > max)
			randomLong = max;
		if (randomLong < min)
			randomLong = min;
		return randomLong;
	}

	public float nextFloat(float min, float max) {
		float randomFloat = random.nextFloat();
		if (randomFloat > max)
			randomFloat = max;
		if (randomFloat < min)
			randomFloat = min;
		return randomFloat;
	}

	public double nextDouble(double min, double max) {
		double randomDouble = random.nextDouble();
		if (randomDouble > max)
			randomDouble = max;
		if (randomDouble < min)
			randomDouble = min;
		return randomDouble;
	}
}