import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import java.util.Timer;
import java.util.TimerTask;

// Main class representing a Trivia Card (demonstrates encapsulation)
public class TriviaCard {
    // Private fields (encapsulation)
    private String question;
    private String answer;
    private List<String> options;
    private Difficulty difficulty;
    private String category;
    private int points;
    private boolean isAnswered;
    private String userAnswer;

    // Enum for difficulty levels
    public enum Difficulty {
        EASY(100, new Color(76, 175, 80)),
        MEDIUM(200, new Color(255, 152, 0)),
        HARD(300, new Color(244, 67, 54)),
        EXPERT(500, new Color(156, 39, 176));

        private final int points;
        private final Color color;

        Difficulty(int points, Color color) {
            this.points = points;
            this.color = color;
        }

        public int getPoints() {
            return points;
        }

        public Color getColor() {
            return color;
        }
    }

    // Constructor
    public TriviaCard(String question, String answer, List<String> options, Difficulty difficulty, String category) {
        this.question = question;
        this.answer = answer;
        this.options = new ArrayList<>(options);
        this.difficulty = difficulty;
        this.category = category;
        this.points = difficulty.getPoints();
        this.isAnswered = false;
        this.userAnswer = null;
    }

    // Public methods to access private fields (encapsulation)
    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    public int getPoints() {
        return points;
    }

    // Method to check answer
    public boolean submitAnswer(String userAnswer) {
        if (isAnswered) {
            throw new IllegalStateException("This question has already been answered!");
        }

        this.isAnswered = true;
        this.userAnswer = userAnswer;
        return checkAnswer();
    }

    // Check if the answer is correct
    private boolean checkAnswer() {
        return userAnswer != null && userAnswer.toLowerCase().equals(answer.toLowerCase());
    }

    // Get card info
    public String getCardInfo() {
        return String.format("""
            Category: %s
            Difficulty: %s
            Points: %d
            Status: %s""",
            category,
            difficulty.name(),
            points,
            isAnswered ? "Answered" : "Not answered"
        );
    }

    // Reset the card
    public void reset() {
        isAnswered = false;
        userAnswer = null;
    }

    // Simple GUI class (demonstrates class relationships)
    static class TriviaGameGUI extends JFrame {
        private List<TriviaCard> cards;
        private int currentCardIndex = 0;
        private int totalScore = 0;
        private JLabel questionLabel;
        private JLabel scoreLabel;
        private JLabel categoryLabel;
        private JPanel buttonPanel;
        private JButton[] optionButtons;
        private JButton nextButton;
        private JProgressBar progressBar;
        private JLabel timerLabel;
        private Timer questionTimer;
        private int timeLeft = 30; // 30 seconds per question
        private final Color BACKGROUND_COLOR = new Color(240, 244, 248);
        private final Color ACCENT_COLOR = new Color(70, 130, 180);
        
        public TriviaGameGUI(List<TriviaCard> cards) {
            this.cards = cards;
            Collections.shuffle(this.cards);
            setupGUI();
            startTimer();
        }

        private void setupGUI() {
            setTitle("Triviamo - Fun Trivia Game");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setMinimumSize(new Dimension(600, 400));
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(BACKGROUND_COLOR);

            // Main panel with modern styling
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            mainPanel.setBackground(BACKGROUND_COLOR);

            // Top panel with timer and progress
            JPanel topPanel = new JPanel(new BorderLayout(20, 0));
            topPanel.setOpaque(false);

            // Timer label with icon
            timerLabel = new JLabel("30s", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            timerLabel.setForeground(ACCENT_COLOR);
            timerLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(ACCENT_COLOR, 2, 20),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            progressBar = new JProgressBar(0, cards.size());
            progressBar.setValue(1);
            progressBar.setStringPainted(true);
            progressBar.setString("Question 1 of " + cards.size());
            progressBar.setFont(new Font("Arial", Font.BOLD, 12));
            progressBar.setForeground(ACCENT_COLOR);
            progressBar.setBackground(Color.WHITE);

            topPanel.add(timerLabel, BorderLayout.WEST);
            topPanel.add(progressBar, BorderLayout.CENTER);

            // Info panel with category and score
            JPanel infoPanel = new JPanel(new BorderLayout(20, 0));
            infoPanel.setOpaque(false);
            
            categoryLabel = new JLabel("Category: ");
            categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            scoreLabel = new JLabel("Score: 0");
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            infoPanel.add(categoryLabel, BorderLayout.WEST);
            infoPanel.add(scoreLabel, BorderLayout.EAST);

            // Question panel with modern styling
            questionLabel = new JLabel();
            questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
            questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionLabel.setForeground(new Color(33, 33, 33));

            // Button panel with modern styling
            buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
            buttonPanel.setOpaque(false);
            optionButtons = new JButton[4];
            
            for (int i = 0; i < 4; i++) {
                optionButtons[i] = new JButton();
                optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
                optionButtons[i].setFocusPainted(false);
                optionButtons[i].setBorder(new RoundedBorder(ACCENT_COLOR, 2, 15));
                optionButtons[i].setBackground(Color.WHITE);
                optionButtons[i].setForeground(new Color(33, 33, 33));
                final int index = i;
                optionButtons[i].addActionListener(e -> checkAnswer(index));
                optionButtons[i].addMouseListener(new ButtonHoverEffect(optionButtons[i]));
                buttonPanel.add(optionButtons[i]);
            }

            // Next button with modern styling
            nextButton = new JButton("Next Question →");
            nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            nextButton.setFont(new Font("Arial", Font.BOLD, 16));
            nextButton.setFocusPainted(false);
            nextButton.setBorder(new RoundedBorder(ACCENT_COLOR, 2, 20));
            nextButton.setBackground(ACCENT_COLOR);
            nextButton.setForeground(Color.WHITE);
            nextButton.setVisible(false);
            nextButton.addActionListener(e -> showNextQuestion());
            nextButton.addMouseListener(new ButtonHoverEffect(nextButton));

            // Add components to main panel
            mainPanel.add(topPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(infoPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(questionLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(buttonPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(nextButton);

            add(mainPanel);
            setLocationRelativeTo(null);
            showQuestion();
        }

        private void startTimer() {
            if (questionTimer != null) {
                questionTimer.cancel();
            }
            timeLeft = 30;
            questionTimer = new Timer();
            questionTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (timeLeft > 0) {
                        timeLeft--;
                        SwingUtilities.invokeLater(() -> {
                            timerLabel.setText(timeLeft + "s");
                            if (timeLeft <= 10) {
                                timerLabel.setForeground(Color.RED);
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> timeUp());
                    }
                }
            }, 0, 1000);
        }

        private void timeUp() {
            questionTimer.cancel();
            for (JButton button : optionButtons) {
                button.setEnabled(false);
            }
            // Show correct answer
            for (JButton button : optionButtons) {
                if (button.getText().equals(cards.get(currentCardIndex).answer)) {
                    button.setBackground(new Color(144, 238, 144));
                    button.setForeground(new Color(0, 100, 0));
                }
            }
            if (currentCardIndex < cards.size() - 1) {
                nextButton.setVisible(true);
            } else {
                showFinalScore();
            }
        }

        // Custom rounded border class
        private class RoundedBorder extends AbstractBorder {
            private final Color color;
            private final int thickness;
            private final int radius;

            public RoundedBorder(Color color, int thickness, int radius) {
                this.color = color;
                this.thickness = thickness;
                this.radius = radius;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(thickness));
                g2d.drawRoundRect(x + thickness/2, y + thickness/2, 
                                width - thickness, height - thickness, 
                                radius, radius);
                g2d.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(radius/2, radius/2, radius/2, radius/2);
            }
        }

        // Button hover effect
        private class ButtonHoverEffect extends MouseAdapter {
            private final JButton button;
            private final Color originalBackground;
            private final Color originalForeground;

            public ButtonHoverEffect(JButton button) {
                this.button = button;
                this.originalBackground = button.getBackground();
                this.originalForeground = button.getForeground();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(ACCENT_COLOR);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalBackground);
                    button.setForeground(originalForeground);
                }
            }
        }

        private void showQuestion() {
            TriviaCard currentCard = cards.get(currentCardIndex);
            questionLabel.setText("<html><body style='width: 400px'>" + currentCard.getQuestion() + "</body></html>");
            categoryLabel.setText("Category: " + currentCard.category + " (" + currentCard.difficulty + ")");
            
            List<String> shuffledOptions = new ArrayList<>(currentCard.getOptions());
            Collections.shuffle(shuffledOptions);
            
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(shuffledOptions.get(i));
                optionButtons[i].setEnabled(true);
                optionButtons[i].setBackground(UIManager.getColor("Button.background"));
                optionButtons[i].setForeground(UIManager.getColor("Button.foreground"));
            }
            
            nextButton.setVisible(false);
            progressBar.setValue(currentCardIndex + 1);
            progressBar.setString("Question " + (currentCardIndex + 1) + " of " + cards.size());
        }

        private void checkAnswer(int buttonIndex) {
            TriviaCard currentCard = cards.get(currentCardIndex);
            String selectedAnswer = optionButtons[buttonIndex].getText();
            boolean isCorrect = currentCard.submitAnswer(selectedAnswer);
            
            for (JButton button : optionButtons) {
                button.setEnabled(false);
            }
            
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].getText().equals(currentCard.answer)) {
                    optionButtons[i].setBackground(new Color(144, 238, 144));  // Light green
                    optionButtons[i].setForeground(new Color(0, 100, 0));     // Dark green text
                } else if (i == buttonIndex && !isCorrect) {
                    optionButtons[i].setBackground(new Color(255, 182, 193));  // Light red
                    optionButtons[i].setForeground(new Color(139, 0, 0));     // Dark red text
                }
            }
            
            if (isCorrect) {
                totalScore += currentCard.getPoints();
                scoreLabel.setText("Score: " + totalScore);
                Toolkit.getDefaultToolkit().beep();
            }
            
            if (currentCardIndex < cards.size() - 1) {
                nextButton.setVisible(true);
            } else {
                showFinalScore();
            }
        }

        private void showNextQuestion() {
            currentCardIndex++;
            showQuestion();
        }

        private void showFinalScore() {
            questionTimer.cancel();
            double percentage = (totalScore * 100.0) / (cards.size() * Difficulty.EASY.getPoints());
            String message = String.format("""
                Game Over!
                
                Final Score: %d points
                Questions Attempted: %d
                Accuracy: %.1f%%
                
                Category Breakdown:
                """, totalScore, cards.size(), percentage);

            // Calculate category statistics
            Map<String, Integer> categoryScores = new HashMap<>();
            Map<String, Integer> categoryTotals = new HashMap<>();
            
            for (TriviaCard card : cards) {
                categoryTotals.merge(card.category, card.points, Integer::sum);
            }
            
            StringBuilder breakdown = new StringBuilder();
            for (Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
                int scored = categoryScores.getOrDefault(entry.getKey(), 0);
                double categoryPercentage = (scored * 100.0) / entry.getValue();
                breakdown.append(String.format("%s: %.1f%%\n", entry.getKey(), categoryPercentage));
            }

            String grade;
            if (percentage >= 90) {
                grade = "Outstanding! You're a trivia master! 🏆";
            } else if (percentage >= 80) {
                grade = "Excellent work! Very knowledgeable! 🌟";
            } else if (percentage >= 70) {
                grade = "Great job! Keep learning! 📚";
            } else if (percentage >= 60) {
                grade = "Good effort! Room for improvement! 💪";
            } else {
                grade = "Keep practicing! You'll get better! 🎯";
            }

            int choice = JOptionPane.showConfirmDialog(
                this,
                message + breakdown.toString() + "\n" + grade + "\n\nWould you like to play again?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                currentCardIndex = 0;
                totalScore = 0;
                scoreLabel.setText("Score: 0");
                Collections.shuffle(cards);
                showQuestion();
                startTimer();
            } else {
                dispose();
            }
        }
    }

    // Main method to demonstrate the program
    public static void main(String[] args) {
        // Create sample trivia cards with more categories and questions
        List<TriviaCard> cards = new ArrayList<>();
        
        // Geography Questions
        cards.add(new TriviaCard(
            "What is the capital of France?",
            "Paris",
            List.of("London", "Paris", "Berlin", "Madrid"),
            Difficulty.EASY,
            "Geography"
        ));

        cards.add(new TriviaCard(
            "Which is the largest country by land area?",
            "Russia",
            List.of("China", "USA", "Russia", "Canada"),
            Difficulty.EASY,
            "Geography"
        ));

        // Science Questions
        cards.add(new TriviaCard(
            "What is the hardest natural substance on Earth?",
            "Diamond",
            List.of("Gold", "Iron", "Diamond", "Platinum"),
            Difficulty.MEDIUM,
            "Science"
        ));

        cards.add(new TriviaCard(
            "What is the chemical symbol for gold?",
            "Au",
            List.of("Ag", "Au", "Fe", "Cu"),
            Difficulty.MEDIUM,
            "Science"
        ));

        // History Questions
        cards.add(new TriviaCard(
            "In which year did World War II end?",
            "1945",
            List.of("1943", "1944", "1945", "1946"),
            Difficulty.MEDIUM,
            "History"
        ));

        cards.add(new TriviaCard(
            "Who was the first President of the United States?",
            "George Washington",
            List.of("Thomas Jefferson", "John Adams", "George Washington", "Benjamin Franklin"),
            Difficulty.EASY,
            "History"
        ));

        // Technology Questions
        cards.add(new TriviaCard(
            "Who co-founded Apple Computer with Steve Jobs?",
            "Steve Wozniak",
            List.of("Bill Gates", "Steve Wozniak", "Mark Zuckerberg", "Jeff Bezos"),
            Difficulty.HARD,
            "Technology"
        ));

        cards.add(new TriviaCard(
            "What programming language was created by James Gosling?",
            "Java",
            List.of("Python", "Java", "C++", "JavaScript"),
            Difficulty.HARD,
            "Technology"
        ));

        // Literature Questions
        cards.add(new TriviaCard(
            "Who wrote 'Romeo and Juliet'?",
            "William Shakespeare",
            List.of("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
            Difficulty.EASY,
            "Literature"
        ));

        cards.add(new TriviaCard(
            "What's the first book of the Harry Potter series?",
            "Harry Potter and the Philosopher's Stone",
            List.of(
                "Harry Potter and the Chamber of Secrets",
                "Harry Potter and the Philosopher's Stone",
                "Harry Potter and the Prisoner of Azkaban",
                "Harry Potter and the Goblet of Fire"
            ),
            Difficulty.MEDIUM,
            "Literature"
        ));

        // Expert Level Questions
        cards.add(new TriviaCard(
            "What is the smallest prime number greater than 100?",
            "101",
            List.of("101", "102", "103", "107"),
            Difficulty.EXPERT,
            "Mathematics"
        ));

        cards.add(new TriviaCard(
            "Which scientist proposed the theory of special relativity?",
            "Albert Einstein",
            List.of("Isaac Newton", "Albert Einstein", "Niels Bohr", "Max Planck"),
            Difficulty.EXPERT,
            "Science"
        ));

        // Launch GUI
        SwingUtilities.invokeLater(() -> {
            TriviaGameGUI game = new TriviaGameGUI(cards);
            game.setVisible(true);
        });
    }
} 