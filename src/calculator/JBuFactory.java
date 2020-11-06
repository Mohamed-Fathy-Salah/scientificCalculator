package calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JBuFactory {
    private EditingListener editingListener;
    private OnClickListener onClickListener;

    public static final int EDITING = 0;
    public static final int AS_IT_IS = 1;
    public static final int ADD_PARENTHESES = 2;
    public static final int MODIFYING = 3;

    public JButton getJButton(int type, String text) {
        if (type == AS_IT_IS) return getAsItIsJButton(text);
        if (type == ADD_PARENTHESES) return getAddParenthesesJButton(text);
        if (type == MODIFYING) return getModifyingJButton(text);
        return getEditingJButton(text);
    }

    private JButton getHelper(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(57,57,57));
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        return button;
    }

    //0..9 . +-*/ () π
    private JButton getAsItIsJButton(String text) {
        JButton button = getHelper(text);
        button.addActionListener(getNewAction(text.charAt(0)));
        return button;
    }

    //sin..tanh log10 exp
    private JButton getAddParenthesesJButton(String text) {
        JButton button = getHelper(text);
        button.addActionListener(getNewAction(text + "("));
        return button;
    }

    //DEL CLR ANS EXIT
    private JButton getEditingJButton(String text) {
        JButton button = getHelper(text);
        button.setBackground(new Color(62,178,78));
        switch (text) {
            case "DEL":
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        editingListener.delete();
                    }
                });
                break;
            case "CLR":
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        editingListener.deleteAll();
                    }
                });
                break;
            case "ANS":
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        editingListener.answer();
                    }
                });
                break;
            case "EXIT":
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        editingListener.exit();
                    }
                });
                break;
        }
        return button;
    }

    //^ √ 1/x ± ncr npr factorial
    private JButton getModifyingJButton(String text) {
        JButton button = getHelper(text);
        switch (text) {
            case "χ2":
                button.addActionListener(getNewAction("^(2)"));
                break;
            case "χ3":
                button.addActionListener(getNewAction("^(3)"));
                break;
            case "x^y":
                button.addActionListener(getNewAction('^'));
                break;
            case "1/x":
                button.addActionListener(getNewAction("1/"));
                break;
            case "√":
                button.addActionListener(getNewAction("(2)√"));
                break;
            case "∛":
                button.addActionListener(getNewAction("(3)√"));
                break;
            case "y√":
                button.addActionListener(getNewAction("√"));
                break;
            case "n!":
                button.addActionListener(getNewAction('!'));
                break;
            case "nPr":
                button.addActionListener(getNewAction('P'));
                break;
            case "nCr":
                button.addActionListener(getNewAction('C'));
                break;
        }
        return button;
    }

    private ActionListener getNewAction(String s) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onClickListener.onClick(s);
            }
        };
    }

    private ActionListener getNewAction(char c) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onClickListener.onClick(c);
            }
        };
    }

    public void setEditingListener(EditingListener editingListener) {
        this.editingListener = editingListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface EditingListener {
        void delete();

        void deleteAll();

        void exit();

        void answer();
    }

    public interface OnClickListener {
        void onClick(String s);

        void onClick(char c);
    }

}
