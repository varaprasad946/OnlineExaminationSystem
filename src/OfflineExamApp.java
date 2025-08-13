import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Offline Examination System (Swing, No Database)
 * Subjects: Basic Coding, Software Engineering, Networking
 * Features: Login, Subject selection, per-question timer, Next/Prev navigation,
 * Shuffle questions, submit & result summary.
 *
 * How to run in IntelliJ:
 *  - Create project → New Java Class named OfflineExamApp
 *  - Paste this file, run the main()
 */
public class OfflineExamApp extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    // Shared state
    private String currentUser = null;
    private ExamSession session = null;

    // Cards
    private final LoginPanel loginPanel = new LoginPanel();
    private final SubjectPanel subjectPanel = new SubjectPanel();
    private final ExamPanel examPanel = new ExamPanel();
    private final ResultPanel resultPanel = new ResultPanel();

    public OfflineExamApp() {
        super("Offline Examination System (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        root.add(loginPanel, "login");
        root.add(subjectPanel, "subject");
        root.add(examPanel, "exam");
        root.add(resultPanel, "result");

        add(root);
        cards.show(root, "login");
    }

    private void startExam(String subjectName) {
        // Prepare questions
        java.util.List<Question> qs = QuestionBank.getQuestions(subjectName);
        Collections.shuffle(qs, new Random());
        session = new ExamSession(subjectName, qs);
        examPanel.loadSession(session);
        cards.show(root, "exam");
    }

    private void showResult() {
        resultPanel.showResult(session);
        cards.show(root, "result");
    }

    // ---------- Panels ----------
    private class LoginPanel extends JPanel {
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JLabel msg = new JLabel(" ");

        LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(8, 8, 8, 8);
            gc.fill = GridBagConstraints.HORIZONTAL;

            JLabel title = new JLabel("Online Examination System (Offline)", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 22));

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints g2 = new GridBagConstraints();
            g2.insets = new Insets(6, 6, 6, 6);
            g2.fill = GridBagConstraints.HORIZONTAL;

            g2.gridx = 0; g2.gridy = 0; form.add(new JLabel("Username:"), g2);
            g2.gridx = 1; form.add(userField, g2);
            g2.gridx = 0; g2.gridy = 1; form.add(new JLabel("Password:"), g2);
            g2.gridx = 1; form.add(passField, g2);

            JButton loginBtn = new JButton("Login");
            loginBtn.addActionListener(e -> {
                String u = userField.getText().trim();
                String p = new String(passField.getPassword());
                // Hardcoded credentials (no DB)
                if ((u.equals("student") && p.equals("123")) || (u.equals("admin") && p.equals("admin"))) {
                    currentUser = u;
                    msg.setText("Login successful. Welcome, " + currentUser + "!");
                    cards.show(root, "subject");
                } else {
                    msg.setText("Invalid credentials. Try student/123 or admin/admin");
                }
            });

            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 1; add(title, gc);
            gc.gridy = 1; add(form, gc);
            gc.gridy = 2; add(loginBtn, gc);
            gc.gridy = 3; msg.setForeground(new Color(180, 0, 0)); add(msg, gc);
        }
    }

    private class SubjectPanel extends JPanel {
        SubjectPanel() {
            setLayout(new BorderLayout(10, 10));
            JLabel title = new JLabel("Select Subject", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            add(title, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
            String[] subjects = {
                    "Basic Coding",
                    "Software Engineering",
                    "Networking"
            };
            for (String s : subjects) {
                JButton b = new JButton(s);
                b.setPreferredSize(new Dimension(220, 90));
                b.addActionListener(e -> startExam(s));
                grid.add(b);
            }

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton logout = new JButton("Logout");
            logout.addActionListener(e -> {
                currentUser = null;
                cards.show(root, "login");
            });
            south.add(logout);

            add(grid, BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);
        }
    }

    private class ExamPanel extends JPanel {
        private JLabel subjectLabel = new JLabel();
        private JLabel timerLabel = new JLabel("Time: 00:00");
        private JTextArea questionArea = new JTextArea();
        private JRadioButton[] optionBtns = new JRadioButton[4];
        private ButtonGroup group = new ButtonGroup();
        private JButton prevBtn = new JButton("Previous");
        private JButton nextBtn = new JButton("Next");
        private JButton clearBtn = new JButton("Clear");
        private JButton submitBtn = new JButton("Submit");
        private JLabel progressLabel = new JLabel("Q 0/0");

        private javax.swing.Timer swingTimer; // 1-second tick

        ExamPanel() {
            setLayout(new BorderLayout(8, 8));

            // Top bar
            JPanel top = new JPanel(new BorderLayout());
            subjectLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            timerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
            top.add(subjectLabel, BorderLayout.WEST);
            top.add(timerLabel, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            // Center: question + options
            JPanel center = new JPanel(new BorderLayout(6, 6));
            questionArea.setWrapStyleWord(true);
            questionArea.setLineWrap(true);
            questionArea.setEditable(false);
            questionArea.setFont(new Font("Serif", Font.PLAIN, 16));
            questionArea.setBorder(BorderFactory.createTitledBorder("Question"));
            center.add(new JScrollPane(questionArea), BorderLayout.CENTER);

            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridLayout(4, 1, 6, 6));
            for (int i = 0; i < 4; i++) {
                optionBtns[i] = new JRadioButton("Option " + (i + 1));
                group.add(optionBtns[i]);
                optionsPanel.add(optionBtns[i]);
            }
            optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
            center.add(optionsPanel, BorderLayout.SOUTH);
            add(center, BorderLayout.CENTER);

            // Bottom: navigation
            JPanel bottom = new JPanel(new BorderLayout());
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
            left.add(progressLabel);
            bottom.add(left, BorderLayout.WEST);

            JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            prevBtn.addActionListener(e -> move(-1));
            nextBtn.addActionListener(e -> move(1));
            clearBtn.addActionListener(e -> {
                group.clearSelection();
                if (session != null) session.selected[session.currentIndex] = -1;
            });
            submitBtn.addActionListener(e -> confirmSubmit());

            controls.add(prevBtn);
            controls.add(nextBtn);
            controls.add(clearBtn);
            controls.add(submitBtn);
            bottom.add(controls, BorderLayout.EAST);

            add(bottom, BorderLayout.SOUTH);
        }

        void loadSession(ExamSession s) {
            this.subjectLabel.setText("Subject: " + s.subjectName);
            updateProgress(s);
            renderQuestion(s);
            // Timer: 60 seconds per question (total)
            int total = s.questions.size() * 60;
            s.secondsLeft = total;
            if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();
            swingTimer = new javax.swing.Timer(1000, e -> {
                s.secondsLeft--;
                updateTimerLabel(s.secondsLeft);
                if (s.secondsLeft <= 0) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    autoSubmit();
                }
            });
            swingTimer.start();
        }

        private void updateTimerLabel(int sec) {
            int m = sec / 60; int s = sec % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", m, s));
        }

        private void renderQuestion(ExamSession s) {
            Question q = s.questions.get(s.currentIndex);
            questionArea.setText((s.currentIndex + 1) + ". " + q.text);
            for (int i = 0; i < 4; i++) {
                optionBtns[i].setText(q.options[i]);
            }
            group.clearSelection();
            int chosen = s.selected[s.currentIndex];
            if (chosen >= 0 && chosen < 4) optionBtns[chosen].setSelected(true);

            prevBtn.setEnabled(s.currentIndex > 0);
            nextBtn.setEnabled(s.currentIndex < s.questions.size() - 1);
        }

        private void move(int delta) {
            if (session == null) return;
            // Save current selection
            for (int i = 0; i < 4; i++) {
                if (optionBtns[i].isSelected()) {
                    session.selected[session.currentIndex] = i;
                    break;
                }
            }
            int ni = session.currentIndex + delta;
            if (ni >= 0 && ni < session.questions.size()) {
                session.currentIndex = ni;
                updateProgress(session);
                renderQuestion(session);
            }
        }

        private void updateProgress(ExamSession s) {
            progressLabel.setText("Q " + (s.currentIndex + 1) + "/" + s.questions.size());
        }

        private void confirmSubmit() {
            int opt = JOptionPane.showConfirmDialog(this, "Submit the exam now?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) doSubmit();
        }

        private void autoSubmit() {
            JOptionPane.showMessageDialog(this, "Time is up! Auto-submitting your exam.");
            doSubmit();
        }

        private void doSubmit() {
            if (swingTimer != null) swingTimer.stop();
            // Capture current selection
            for (int i = 0; i < 4; i++) {
                if (optionBtns[i].isSelected()) {
                    session.selected[session.currentIndex] = i;
                    break;
                }
            }
            session.evaluate();
            showResult();
        }
    }

    private class ResultPanel extends JPanel {
        private JLabel summary = new JLabel(" ", SwingConstants.CENTER);
        private JTextArea details = new JTextArea();

        ResultPanel() {
            setLayout(new BorderLayout(8, 8));
            JLabel title = new JLabel("Result Summary", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 20));
            add(title, BorderLayout.NORTH);

            details.setEditable(false);
            details.setFont(new Font("Monospaced", Font.PLAIN, 14));

            JPanel center = new JPanel(new BorderLayout());
            center.add(summary, BorderLayout.NORTH);
            center.add(new JScrollPane(details), BorderLayout.CENTER);
            add(center, BorderLayout.CENTER);

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton again = new JButton("Take Another Subject");
            JButton logout = new JButton("Logout");

            again.addActionListener(e -> cards.show(root, "subject"));
            logout.addActionListener(e -> cards.show(root, "login"));

            south.add(again);
            south.add(logout);
            add(south, BorderLayout.SOUTH);
        }

        void showResult(ExamSession s) {
            String name = (currentUser == null ? "Student" : currentUser);
            summary.setText("User: " + name + "    Subject: " + s.subjectName +
                    "    Score: " + s.score + "/" + s.questions.size());

            StringBuilder sb = new StringBuilder();
            int qno = 1;
            for (int i = 0; i < s.questions.size(); i++) {
                Question q = s.questions.get(i);
                int sel = s.selected[i];
                sb.append(String.format("Q%d: %s\n", qno++, q.text));
                sb.append(String.format("   Correct: %s\n", q.options[q.correctIndex]));
                sb.append(String.format("   Your Ans: %s\n\n", (sel >= 0 ? q.options[sel] : "(not attempted)")));
            }
            details.setText(sb.toString());
        }
    }

    // ---------- Model ----------
    private static class ExamSession {
        final String subjectName;
        final java.util.List<Question> questions;
        final int[] selected; // -1 if not selected
        int currentIndex = 0;
        int score = 0;
        int secondsLeft = 0;

        ExamSession(String subjectName, java.util.List<Question> questions) {
            this.subjectName = subjectName;
            this.questions = questions;
            this.selected = new int[questions.size()];
            Arrays.fill(this.selected, -1);
        }

        void evaluate() {
            int sc = 0;
            for (int i = 0; i < questions.size(); i++) {
                if (selected[i] == questions.get(i).correctIndex) sc++;
            }
            this.score = sc;
        }
    }

    private static class Question {
        final String text;
        final String[] options;
        final int correctIndex; // 0..3

        Question(String text, String[] options, int correctIndex) {
            if (options.length != 4) throw new IllegalArgumentException("Exactly 4 options required");
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }

    private static class QuestionBank {
        static java.util.List<Question> getQuestions(String subject) {
            switch (subject) {
                case "Basic Coding":
                    return basicCoding();
                case "Software Engineering":
                    return softwareEngineering();
                case "Networking":
                    return networking();
                default:
                    return new ArrayList<>();
            }
        }

        private static java.util.List<Question> basicCoding() {
            java.util.List<Question> qs = new ArrayList<>();
            qs.add(new Question(
                    "Which symbol is used for single-line comments in Java?",
                    new String[]{"#", "//", "/* */", "--"}, 1));
            qs.add(new Question(
                    "What will be printed: int a=3,b=2; System.out.println(a+b*2);",
                    new String[]{"10", "7", "8", "5"}, 1));
            qs.add(new Question(
                    "Which data structure uses FIFO order?",
                    new String[]{"Stack", "Queue", "Tree", "Graph"}, 1));
            qs.add(new Question(
                    "Which keyword creates a new object in Java?",
                    new String[]{"new", "create", "make", "alloc"}, 0));
            qs.add(new Question(
                    "Which loop runs at least once regardless of condition?",
                    new String[]{"for", "while", "do-while", "foreach"}, 2));
            qs.add(new Question(
                    "Which method is entry point of a Java program?",
                    new String[]{"start()", "run()", "main(String[] args)", "init()"}, 2));
            qs.add(new Question(
                    "Which of these is NOT a primitive type in Java?",
                    new String[]{"int", "boolean", "double", "String"}, 3));
            qs.add(new Question(
                    "What does '==' compare for primitive types?",
                    new String[]{"Values", "References", "Hash codes", "Memory addresses only"}, 0));
            return qs;
        }

        private static java.util.List<Question> softwareEngineering() {
            java.util.List<Question> qs = new ArrayList<>();
            qs.add(new Question(
                    "Which SDLC model is best when requirements frequently change?",
                    new String[]{"Waterfall", "V-Model", "Spiral", "Agile"}, 3));
            qs.add(new Question(
                    "In Scrum, a time-boxed iteration is called a...",
                    new String[]{"Module", "Sprint", "Epic", "Story"}, 1));
            qs.add(new Question(
                    "SRS stands for...",
                    new String[]{"Software Result Sheet", "System Requirement Set", "Software Requirements Specification", "Standard Requirement Spec"}, 2));
            qs.add(new Question(
                    "Which of these is NOT a good design principle?",
                    new String[]{"High cohesion", "Low coupling", "Tight coupling", "Separation of concerns"}, 2));
            qs.add(new Question(
                    "Which UML diagram shows object interactions over time?",
                    new String[]{"Use Case", "Class", "Sequence", "Component"}, 2));
            qs.add(new Question(
                    "Validation ensures...",
                    new String[]{"you built the product right", "you built the right product", "code compiles", "tests are automated"}, 1));
            qs.add(new Question(
                    "Black-box testing focuses on...",
                    new String[]{"code structure", "performance only", "functional behavior without internal details", "unit internals"}, 2));
            qs.add(new Question(
                    "The V-Model primarily emphasizes...",
                    new String[]{"Rapid prototyping", "Mapping testing to development phases", "Customer collaboration only", "No documentation"}, 1));
            return qs;
        }

        private static java.util.List<Question> networking() {
            java.util.List<Question> qs = new ArrayList<>();
            qs.add(new Question(
                    "Which OSI layer is responsible for routing?",
                    new String[]{"Data Link", "Network", "Transport", "Session"}, 1));
            qs.add(new Question(
                    "Which protocol provides reliable, connection-oriented transport?",
                    new String[]{"UDP", "IP", "TCP", "ICMP"}, 2));
            qs.add(new Question(
                    "Default port for HTTP is...",
                    new String[]{"20", "21", "25", "80"}, 3));
            qs.add(new Question(
                    "A device that connects multiple networks and forwards packets is a...",
                    new String[]{"Hub", "Switch", "Router", "Repeater"}, 2));
            qs.add(new Question(
                    "Which of the following is NOT an application-layer protocol?",
                    new String[]{"HTTP", "FTP", "SMTP", "ARP"}, 3));
            qs.add(new Question(
                    "DNS is used for...",
                    new String[]{"Encrypting data", "Resolving domain names to IP addresses", "Routing decisions", "Flow control"}, 1));
            qs.add(new Question(
                    "The subnet mask for a /24 network is...",
                    new String[]{"255.0.0.0", "255.255.0.0", "255.255.255.0", "255.255.255.128"}, 2));
            qs.add(new Question(
                    "Which layer handles end-to-end process-to-process communication in OSI?",
                    new String[]{"Session", "Transport", "Network", "Application"}, 1));
            return qs;
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OfflineExamApp app = new OfflineExamApp();
            app.setVisible(true);
        });
    }
}
