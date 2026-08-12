package com.amdocs.telecom.security;

import java.util.Random;

public class CaptchaGenerator {
    private static CaptchaGenerator instance;
    private final Random random = new Random();

    private CaptchaGenerator() {}

    public static synchronized CaptchaGenerator getInstance() {
        if (instance == null) {
            instance = new CaptchaGenerator();
        }
        return instance;
    }

    public CaptchaChallenge generateChallenge() {
        int a = random.nextInt(20) + 1;
        int b = random.nextInt(20) + 1;
        String question = String.format("Solve CAPTCHA: What is %d + %d?", a, b);
        String answer = String.valueOf(a + b);
        return new CaptchaChallenge(question, answer);
    }

    public static class CaptchaChallenge {
        private final String question;
        private final String answer;

        public CaptchaChallenge(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
        public boolean validate(String userInput) {
            return userInput != null && userInput.trim().equalsIgnoreCase(answer.trim());
        }
    }
}
