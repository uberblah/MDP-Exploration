package com.uberblah.school.gatech.ml.projects.markov.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

public class RandomChooser<T> {
    private Random random;
    private double totalWeight;
    private List<Choice<T>> choices;

    public RandomChooser(List<Choice<T>> choices) {
        this(choices, null);
    }

    public RandomChooser(List<Choice<T>> choices, Long seed) {
        this.totalWeight = 0.0d;
        for (Choice<T> choice : choices) {
            this.totalWeight += choice.getWeight();
        }
        this.choices = choices;
        if (seed == null) {
            this.random = new Random();
        } else {
            this.random = new Random(seed);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Choice<T> {
        private T value;
        private double weight;
    }

    public T sample() {
        double num = random.nextDouble() * totalWeight;
        double sum = 0.0d;
        for (Choice<T> choice : choices) {
            sum += choice.weight;
            if (num < sum) {
                return choice.value;
            }
        }
        throw new IllegalStateException("What the heck?");
    }
}
