package it.unibo.oop.lab.lambda.ex03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * 
 * 1) Convert to lowercase
 * 
 * 2) Count the number of chars
 * 
 * 3) Count the number of lines
 * 
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    private static final long serialVersionUID = 1760990730218643730L;
    private static final String PATTERN = "\\W+";

    private enum Command {
        IDENTITY("No modifications", Function.identity()),
        LOWERCASE("Convert to lowercase", String::toLowerCase),
        CHAR_COUNT("Count all characters", s -> String.valueOf(s.chars()
                                                      .count())),
        LINE_COUNT("Count all lines", s -> String.valueOf(s.chars()
                                            .filter(c -> c == '\n' || c == '\r')
                                            .count() + 1)), //counting the last empty line if it's present
        ALPHABETICAL("Sort all words by alphabetical order", s -> Pattern.compile(PATTERN)
                                                                    .splitAsStream(s)
                                                                    .filter(w -> !w.isBlank())
                                                                    .distinct()
                                                                    .sorted()
                                                                    .collect(joining("\n"))),
        OCCURRENCES("Count all occurrences of each word", s -> Pattern.compile(PATTERN)
                                                                  .splitAsStream(s)
                                                                  .filter(w -> !w.isBlank())
                                                                  .collect(toMap(w -> w, w -> 1, Integer::sum))
                                                                  .entrySet()
                                                                  .stream()
                                                                  .map(e -> e.getKey() + " -> " + e.getValue())
                                                                  .collect(joining("\n")));

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            this.commandName = name;
            this.fun = process;
        }

        public String toString() {
            return this.commandName;
        }

        public String translate(final String s) {
            return this.fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
