import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

/**
 * Small flat/modern look for the whole app: palette, fonts and a handful of
 * custom-painted Swing components (rounded cards, buttons, chips, stepper).
 * Pure Swing - no third-party library.
 */
public final class Theme {
    private Theme() {}

    public static final Color BG = new Color(0xF3F4F8);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(0x4F46E5);
    public static final Color PRIMARY_DARK = new Color(0x4338CA);
    public static final Color PRIMARY_SOFT = new Color(0xE0E7FF);
    public static final Color TEXT = new Color(0x111827);
    public static final Color MUTED = new Color(0x6B7280);
    public static final Color BORDER = new Color(0xE5E7EB);
    public static final Color OK = new Color(0x047857);
    public static final Color OK_SOFT = new Color(0xD1FAE5);
    public static final Color WARN = new Color(0xB45309);
    public static final Color WARN_SOFT = new Color(0xFEF3C7);
    public static final Color BAD = new Color(0xB91C1C);
    public static final Color BAD_SOFT = new Color(0xFEE2E2);

    private static String family = "SansSerif";

    static {
        String[] wanted = {"Inter", "Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto", "Arial"};
        java.util.Set<String> have = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String w : wanted) {
            if (have.contains(w)) {
                family = w;
                break;
            }
        }
    }

    public static Font font(int style, float size) {
        return new Font(family, style, 1).deriveFont(style, size);
    }

    /** Applies fonts and base colours to every Swing default. Call once before creating windows. */
    public static void install() {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object k = keys.nextElement();
            if (k.toString().endsWith(".font")) {
                UIManager.put(k, new FontUIResource(font(Font.PLAIN, 13f)));
            }
        }
        UIManager.put("Panel.background", BG);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    private static void quality(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }

    // ------------------------------------------------------------------ Card

    /** White rounded panel with a soft drop shadow. */
    public static class Card extends JPanel {
        private final int radius;
        private Color fill = CARD;

        public Card(LayoutManager lm, int radius, int pad) {
            super(lm);
            this.radius = radius;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(pad, pad, pad + 3, pad));
        }

        public Card fill(Color c) {
            fill = c;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            quality(g);
            int w = getWidth(), h = getHeight() - 3;
            for (int i = 3; i >= 1; i--) {
                g.setColor(new Color(17, 24, 39, 6 + (4 - i) * 2));
                g.fill(new RoundRectangle2D.Float(-i + 1, i, w + 2 * i - 2, h + 1, radius + i, radius + i));
            }
            g.setColor(fill);
            g.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));
            g.setColor(BORDER);
            g.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));
            g.dispose();
            super.paintComponent(g0);
        }
    }

    // ---------------------------------------------------------------- Button

    public enum Kind { PRIMARY, SECONDARY, GHOST, DANGER }

    /** Flat rounded button with hover / pressed states. */
    public static class Btn extends JButton {
        private Kind kind;
        private boolean selected;
        private boolean hover;

        public Btn(String text, Kind kind) {
            super(text);
            this.kind = kind;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(font(Font.BOLD, 13f));
            setMargin(new Insets(8, 16, 8, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        public void setKind(Kind k) { kind = k; repaint(); }

        /** Used by pill toggles: selected pills are drawn in the primary colour. */
        public void setSelectedLook(boolean s) { selected = s; repaint(); }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(getText()) + 36, 38);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            quality(g);
            boolean pressed = getModel().isArmed();
            Color bg, fg, line = null;
            Kind k = selected ? Kind.PRIMARY : kind;
            switch (k) {
                case PRIMARY:
                    bg = pressed ? PRIMARY_DARK : hover ? new Color(0x6366F1) : PRIMARY;
                    fg = Color.WHITE;
                    break;
                case DANGER:
                    bg = hover ? BAD_SOFT : Color.WHITE;
                    fg = BAD;
                    line = new Color(0xFCA5A5);
                    break;
                case GHOST:
                    bg = hover ? new Color(0xE5E7EB) : new Color(0, 0, 0, 0);
                    fg = TEXT;
                    break;
                default:
                    bg = pressed ? new Color(0xE5E7EB) : hover ? new Color(0xF3F4F6) : Color.WHITE;
                    fg = TEXT;
                    line = BORDER;
            }
            if (!isEnabled()) {
                bg = new Color(0xF3F4F6);
                fg = new Color(0x9CA3AF);
            }
            int arc = 14;
            g.setColor(bg);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
            if (line != null) {
                g.setColor(line);
                g.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, arc, arc));
            }
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            g.setColor(fg);
            g.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }

    // ------------------------------------------------------------------ Chip

    /** Small rounded status label. */
    public static class Chip extends JComponent {
        private String text;
        private Color bg, fg;

        public Chip(String text, Color bg, Color fg) {
            this.text = text;
            this.bg = bg;
            this.fg = fg;
            setFont(font(Font.BOLD, 11f));
        }

        public void set(String text, Color bg, Color fg) {
            this.text = text;
            this.bg = bg;
            this.fg = fg;
            revalidate();
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            return new Dimension(fm.stringWidth(text) + 20, 22);
        }

        @Override
        public Dimension getMaximumSize() { return getPreferredSize(); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            quality(g);
            g.setColor(bg);
            g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            g.setColor(fg);
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }

    // --------------------------------------------------------------- Stepper

    /** [-] value [+] control. */
    public static class Stepper extends JPanel {
        private int value, min, max;
        private final JLabel valueLabel = label("", Font.BOLD, 14f, TEXT);
        private Runnable onChange = () -> {};

        public Stepper(int value, int min, int max) {
            super(new FlowLayout(FlowLayout.CENTER, 6, 0));
            this.value = value;
            this.min = min;
            this.max = max;
            setOpaque(false);
            Btn minus = small("-");
            Btn plus = small("+");
            minus.addActionListener(e -> set(this.value - 1));
            plus.addActionListener(e -> set(this.value + 1));
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            valueLabel.setPreferredSize(new Dimension(28, 28));
            add(minus);
            add(valueLabel);
            add(plus);
            render();
        }

        private static Btn small(String t) {
            Btn b = new Btn(t, Kind.SECONDARY) {
                @Override public Dimension getPreferredSize() { return new Dimension(28, 28); }
            };
            b.setFont(font(Font.BOLD, 14f));
            return b;
        }

        public void setOnChange(Runnable r) { onChange = r; }
        public int getValue() { return value; }

        public void setValue(int v) {
            value = Math.max(min, Math.min(max, v));
            render();
        }

        public void setMax(int m) { max = m; }

        private void set(int v) {
            int old = value;
            setValue(v);
            if (old != value) onChange.run();
        }

        private void render() { valueLabel.setText(String.valueOf(value)); }

        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
    }

    // ----------------------------------------------------------- Text field

    public static JTextField field(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(font(Font.PLAIN, 14f));
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 12),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        return f;
    }

    /** 1px rounded outline border. */
    public static class RoundedLineBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int arc;

        public RoundedLineBorder(Color c, int arc) {
            this.color = c;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g0, int x, int y, int w, int h) {
            Graphics2D g = (Graphics2D) g0.create();
            quality(g);
            g.setColor(color);
            g.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, arc, arc));
            g.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
    }

    /** Thin horizontal divider. */
    public static JComponent divider() {
        JComponent d = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDER);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        d.setPreferredSize(new Dimension(10, 1));
        d.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return d;
    }
}
