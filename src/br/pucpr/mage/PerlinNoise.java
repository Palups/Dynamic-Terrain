package br.pucpr.mage;

import java.util.Random;

/**
 * Created by maiki on 24/06/2017.
 */
public class PerlinNoise {
    int seed;
    int height, width;
    float[][] noise;

    public PerlinNoise(int x, int y, int seed) {
        this.width = x;
        this.height = y;
        this.seed = seed;

        noise = GeneratePerlinNoise(GenerateWhiteNoise(width, height), 6);
    }

    float Interpolate(float n, float m, float alpha)
    {
        return n * (1 - alpha) + alpha * m;
    }


    public float[][] GenerateWhiteNoise(int width, int height)
    {
        Random random = new Random(seed);
        float[][] noise = new float[width][height];

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                noise[i][j] = (float)random.nextDouble() % 1;
            }
        }

        return noise;
    }

    float[][] GenerateSmoothNoise(float[][] baseNoise, int octave)
    {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][] smoothNoise = new float[width][height];

        int samplePeriod = (int) Math.pow(2, octave);
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++)
        {

            int i0 = (i / samplePeriod) * samplePeriod;
            int i1 = (i0 + samplePeriod) % width; //wrap around
            float horizontal_blend = (i - i0) * sampleFrequency;

            for (int j = 0; j < height; j++)
            {
                int j0 = (j / samplePeriod) * samplePeriod;
                int j1 = (j0 + samplePeriod) % height; //wrap around
                float vertical_blend = (j - j0) * sampleFrequency;

                float top = Interpolate(baseNoise[i0][j0], baseNoise[i1][j0], horizontal_blend);

                float bottom = Interpolate(baseNoise[i0][j1], baseNoise[i1][j1], horizontal_blend);

                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }

        return smoothNoise;
    }

    float[][] GeneratePerlinNoise(float[][] baseNoise, int octaveCount)
    {
        int width = baseNoise.length;
        int height = baseNoise[0].length;

        float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing

        float persistance = 0.4f;

        for (int i = 0; i < octaveCount; i++)
        {
            smoothNoise[i] = GenerateSmoothNoise(baseNoise, i);
        }

        float[][] perlinNoise = new float[width][height];
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        for (int octave = octaveCount - 1; octave >= 0; octave--)
        {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    perlinNoise[i][j] += smoothNoise[octave][i][j] * amplitude;
                }
            }
        }

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                perlinNoise[i][j] /= totalAmplitude;
            }
        }

        return perlinNoise;
    }


    public float [][] GetNoise()
    {
        return noise;
    }
}
