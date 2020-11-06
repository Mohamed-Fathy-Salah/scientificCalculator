package calculator;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class CalculatorFrame extends JFrame {
    private JPanel mainPanel, northPanel, northWest, northEast, middlePanel, eastPanel, westPanel, footerPanel;
    private JCheckBox checkBox;
    private JTextField equationField;
    private JTextArea resultArea;
    private int smallestX, smallestY;
    private JBuFactory jBuFact;
    private Stack<Integer> stack;
    private ScriptEngine engine;
    private String convertedString;
    private AppendHelper appendHelper;

    public CalculatorFrame() {
        super("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        createMenuBar();
        initialize();
        settings();
        adding();

        getContentPane().add(mainPanel);
        setLocationRelativeTo(null);  // center the frame in the screen
        pack();

        setMinimumSize(getSize());
        smallestX = getWidth();
        smallestY = getHeight();

        addListeners();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        ImageIcon exitIcon = new ImageIcon(((new ImageIcon("src/resources/exit.png")).getImage()).getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        // to appear file when you click alt+f

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        // to appear help when you click alt+

        JMenuItem exitItem = new JMenuItem("Exit", exitIcon);
        exitItem.setMnemonic(KeyEvent.VK_E);
        // to close the program when you click alt+E
        exitItem.setToolTipText("Exit application");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });

        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void initialize() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");

        appendHelper = new AppendHelper();

        mainPanel = new JPanel();
        resultArea = new JTextArea();
        equationField = new JTextField();
        northPanel = new JPanel(new BorderLayout());
        northEast = new JPanel();
        northWest = new JPanel();
        middlePanel = new JPanel();
        westPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        eastPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBox = new JCheckBox("Scientific");

        jBuFact = new JBuFactory();
        jBuFact.setEditingListener(new JBuFactory.EditingListener() {
            @Override
            public void delete() {
                erase();
            }

            @Override
            public void deleteAll() {
                eraseAll();
            }

            @Override
            public void exit() {
                close();
            }

            @Override
            public void answer() {
                result();
            }
        });
        jBuFact.setOnClickListener(new JBuFactory.OnClickListener() {
            @Override
            public void onClick(String s) {
                append(s);
            }

            @Override
            public void onClick(char c) {
                append(c);
            }
        });
        stack = new Stack<>();
    }

    private void settings() {

        try {
            engine.eval("var pi=Math.PI;\n" +
                    "function isNotInt(n){return Math.ceil(n)>n;}\n" +
                    "function fact(n){if(isNotInt(n)) throw 'Math error : factorial is for integers only';if(n<0)throw 'Math error : getting factorial of -ve number';if (n<2) return 1;return n*fact(n-1);}\n" +
                    "function ncr(n,r) {if(r<n)return fact(n) / (fact(r) * fact(n - r));}\n" +
                    "function npr(n,r) {if(r<n)return fact(n) /fact(n - r);}\n" +
                    "function exp(n){return Math.exp(n);}\n" +
                    "function sin(n){return Math.round(1000*Math.sin(n*Math.PI/180))/1000;}\n" +
                    "function cos(n){return Math.round(1000*Math.cos(n*Math.PI/180))/1000;}\n" +
                    "function tan(n){ if(Math.abs(n%180) == 90) throw 'Math error : tan(90) is infinity'; else return Math.round(1000*Math.tan(n*Math.PI/180))/1000;}\n" +
                    "function asin(n){if (n<-1 || n>1) throw 'Math error : asin is between -1 and 1'; else {return Math.round(1000*Math.asin(n)*(180/Math.PI))/1000;}}\n" +
                    "function acos(n){if (n<-1 || n>1) throw 'Math error : acos is between -1 and 1'; else {return Math.round(1000*Math.acos(n)*(180/Math.PI))/1000;}}\n" +
                    "function atan(n){ return Math.round(1000*Math.atan(n)*(180/Math.PI))/1000;}\n" +
                    "function sinh(n){return (exp(n)-exp(-1*n))/2;}\n" +
                    "function cosh(n){return (exp(n)+exp(-1*n))/2;}\n" +
                    "function tanh(n){return (exp(n)-exp(-1*n))/(exp(n)+exp(-1*n));}\n" +
                    "function log(n){if(n<=0)throw 'Math error : n is -ve or 0  in log('+n+')' ;else return Math.log(n);}\n" +
                    "function log10(n){if(n<=0)throw 'Math error : n is -ve or 0 in log10('+n+')' ;else return Math.round(1000*(log(n)/log(10))/1000);}\n" +
                    "function pow(x,r){return Math.round(1000*Math.pow(x,r))/1000;}");
        } catch (ScriptException e) {
            // e.printStackTrace();
        }

        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(boxLayout);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(58, 58, 58));

        resultArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        resultArea.setEnabled(false);
        resultArea.setBackground(new Color(47, 47, 47));
        resultArea.setDisabledTextColor(Color.WHITE);

        //setBorder acts as padding .. up,left,down,right
        equationField.setBorder(new EmptyBorder(10, 10, 10, 10));
        equationField.setEnabled(false);
        equationField.setBackground(new Color(50, 50, 50));
        equationField.setDisabledTextColor(Color.WHITE);

        northPanel.setOpaque(false);
        northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        northEast.setOpaque(false);
        northWest.setOpaque(false);

        BoxLayout boxLayout1 = new BoxLayout(middlePanel, BoxLayout.X_AXIS);
        middlePanel.setLayout(boxLayout1);
        middlePanel.setOpaque(false);

        westPanel.setOpaque(false);
        westPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        eastPanel.setOpaque(false);
        eastPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        eastPanel.setVisible(false);

        //for not drawing the background of the flowLayout
        checkBox.setOpaque(false);
        checkBox.setForeground(Color.white);
        //to make focus on frame only that makes the keyListener works all the time
        checkBox.setFocusable(false);

        footerPanel.setOpaque(false);
    }

    private void adding() {
        northWest.add(jBuFact.getJButton(JBuFactory.EDITING, "DEL"));
        northWest.add(jBuFact.getJButton(JBuFactory.EDITING, "CLR"));
        northEast.add(jBuFact.getJButton(JBuFactory.EDITING, "ANS"));
        northEast.add(jBuFact.getJButton(JBuFactory.EDITING, "EXIT"));

        northPanel.add(northWest, BorderLayout.WEST);
        northPanel.add(northEast, BorderLayout.EAST);

        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "7"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "8"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "9"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "+"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "4"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "5"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "6"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "-"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "1"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "2"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "3"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "*"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "0"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "."));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "±"));
        westPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "/"));

        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "χ2"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "χ3"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "x^y"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "1/x"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "("));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "√"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "∛"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "y√"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "n!"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, ")"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "sin"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "cos"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "tan"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "exp"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "nPr"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "asin"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "acos"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "atan"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "log"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.MODIFYING, "nCr"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "sinh"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "cosh"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "tanh"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.ADD_PARENTHESES, "log10"));
        eastPanel.add(jBuFact.getJButton(JBuFactory.AS_IT_IS, "π"));

        middlePanel.add(westPanel);
        middlePanel.add(eastPanel);

        footerPanel.add(checkBox);

        mainPanel.add(resultArea);
        mainPanel.add(equationField);
        mainPanel.add(northPanel);
        mainPanel.add(middlePanel);
        mainPanel.add(footerPanel);
    }

    private void addListeners() {
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                eastPanel.setVisible(itemEvent.getStateChange() == ItemEvent.SELECTED);

                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    // pack : to adjust the size before using getSize method
                    pack();
                    setMinimumSize(getSize());
                } else {
                    setMinimumSize(new Dimension(smallestX, smallestY));
                    // pack : to adjust the size of the frame after changing the size
                    pack();
                }
            }
        });
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                char tmp = keyEvent.getKeyChar();
                if (isOk(tmp) || tmp == 'p' || tmp == 'c')
                    append(Character.toUpperCase(tmp));
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getExtendedKeyCode() == KeyEvent.VK_BACK_SPACE)
                    erase();
                if (keyEvent.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    result();
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });
    }

    private char getLastChar() {
        char lastCharInText;
        try {
            lastCharInText = equationField.getText().charAt((getEquationLength() - 1));
        } catch (StringIndexOutOfBoundsException e) {
            lastCharInText = ' ';
        }
        return lastCharInText;
    }

    //for searching in the appendHelper class
    private char convertChar(char c) {
        if (Character.isDigit(c)) return '0';
        if (isOperation(c)) return '+';
        if (isOKChar(c)) return 'P';
        return c;
    }

    private void append(String input) {
        char lastCharInText = getLastChar();
        if (appendHelper.check(convertChar(lastCharInText), convertChar(input.charAt(0))) == AppendHelper.RETURN)
            return;
        if (input.equals("1/") && (lastCharInText == '.' || lastCharInText == '!'
                || lastCharInText == 'π' || Character.isDigit(lastCharInText))) {
            stack.push(1);
            equationField.setText(equationField.getText() + "/");
            return;
        }
        if (Character.isLowerCase(input.charAt(0)) && !isOperation(lastCharInText) && lastCharInText != ' ' && lastCharInText != '(') {
            stack.push(1);
            stack.push(input.length());
            equationField.setText(equationField.getText() + "*" + input);
            return;
        }
        stack.push(input.length());
        equationField.setText(equationField.getText() + input);
    }

    private void append(char input) {
        char lastCharInText = getLastChar();

        int checkedResult = appendHelper.check(convertChar(lastCharInText), convertChar(input));

        if (checkedResult == AppendHelper.RETURN) return;
        if (checkedResult == AppendHelper.MULTIPLY) {
            stack.push(1);
            stack.push(1);
            equationField.setText(equationField.getText() + "*" + input);
            return;
        }
        if (input == '.') {
            if (!stack.empty() && stack.peek() == -1) return;
            stack.push(-1);
            equationField.setText(equationField.getText() + '.');
            return;
        }
        if (Character.isDigit(input)) {
            stack.push((!stack.empty() && stack.peek() == -1) ? -1 : 1);
            equationField.setText(equationField.getText() + input);
            return;
        }
        if (input == '±') {
            if (lastCharInText == '+') {
                equationField.setText(equationField.getText().substring(0, getEquationLength() - 1) + '-');
                return;
            }
            if (lastCharInText == '-') {
                equationField.setText(equationField.getText().substring(0, getEquationLength() - 1) + '+');
                return;
            }
            stack.push(1);
            equationField.setText(equationField.getText() + '+');
            return;
        }
        stack.push(1);
        equationField.setText(equationField.getText() + input);
    }

    private int getEquationLength() {
        return equationField.getText().length();
    }

    private Boolean isOk(char c) {
        return Character.isDigit(c) || isOKChar(c) || isOperation(c) || isParentheses(c) || c == 'π' || c == '!' || c == '.';
    }

    private Boolean isOKChar(char c) {
        return c == 'P' || c == 'C';
    }

    private Boolean isParentheses(char c) {
        return c == '(' || c == ')';
    }

    private Boolean isOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private void erase() {
        if (getEquationLength() == 0) eraseAll();
        else
            equationField.setText(
                    equationField.getText().substring(
                            0, getEquationLength() - Math.abs(stack.pop())));
    }

    private void eraseAll() {
        stack.clear();
        equationField.setText("");
        resultArea.setText("");
    }

    private void close() {
        System.exit(0);
    }

    private void convertString() throws ScriptException {
        convertedString = equationField.getText();



        //check if the parentheses are balanced
        Stack<Boolean> tmpStack = new Stack<>();
        for (int i = 0; i < convertedString.length(); i++) {
            if (convertedString.charAt(i) == '(') tmpStack.push(true);
            else if (convertedString.charAt(i) == ')') {
                if (tmpStack.empty()) throw new ScriptException("parentheses are not mirroring () at index " + i);
                tmpStack.pop();
            }
        }
        //complete the closing parentheses )
        for (int i = 0; i < tmpStack.size(); i++) convertedString += ')';

        String before, after = "", tmp = "PC^√!";
        int index;
        char hold;
        for (int i = 0; i < getEquationLength(); i++) {
            hold = equationField.getText().charAt(i);
            if (tmp.contains(Character.toString(hold))) {
                index = convertedString.indexOf(hold);
                before = getBefore(index);
                try {
                    after = getAfter(index);
                } catch (Exception ignored) {
                }

                if (hold == 'P')
                    convertedString = convertedString.replace(before + "P" + after, "npr(" + before + "," + after + ")");
                else if (hold == 'C')
                    convertedString = convertedString.replace(before + "C" + after, "ncr(" + before + "," + after + ")");
                else if (hold == '^')
                    convertedString = convertedString.replace(before + "^" + after, "pow(" + before + "," + after + ")");
                else if (hold == '√')
                    convertedString = convertedString.replace(before + "√" + after, "pow(" + after + ",1.0/" + before + ")");
                else if (hold == '!')
                    convertedString = convertedString.replace(before + "!", "fact(" + before + ")");
            }
        }
        convertedString = convertedString.replaceAll("π", "pi");
    }

    private String getBefore(int index) {
        int i = index - 1;
        if (convertedString.charAt(i) == ')') {
            Stack<Boolean> tempStack = new Stack<>();
            tempStack.push(true);
            for (--i; i > -1 && !tempStack.empty(); i--) {
                if (convertedString.charAt(i) == ')') tempStack.push(true);
                else if (convertedString.charAt(i) == '(') tempStack.pop();
            }
            while (i > -1 && !isOperation(convertedString.charAt(i))) {
                i--;
            }
        } else {
            for (; i > -1 && !isOperation(convertedString.charAt(i)) && convertedString.charAt(i) != ',' && convertedString.charAt(i) != '('; i--)
                ;
        }
        return convertedString.substring(i + 1, index);
    }

    private String getAfter(int index) {
        int i = index + 1;
        if (convertedString.charAt(i) == '(') {
            Stack<Boolean> tempStack = new Stack<>();
            tempStack.push(true);
            for (++i; i < getEquationLength() && !tempStack.empty(); i++) {
                if (convertedString.charAt(i) == '(') tempStack.push(true);
                else if (convertedString.charAt(i) == ')') tempStack.pop();
            }
        } else {
            for (; i < convertedString.length() && !isOperation(convertedString.charAt(i)) && !isOKChar(convertedString.charAt(i)) && convertedString.charAt(i) != ')'; i++)
                ;
        }
        return convertedString.substring(index + 1, i);
    }

    private void result() {
        try {
            convertString();
            resultArea.setDisabledTextColor(Color.white);
            String evaluated = engine.eval(convertedString).toString();
            if (evaluated.equals("NaN")) throw new Exception();
            if (evaluated.equals("Infinity") || evaluated.equals("-Infinity")) throw new Exception(evaluated);
            resultArea.setText(evaluated);
        } catch (ScriptException e) {
            resultArea.setDisabledTextColor(Color.RED);
            if (e.getMessage().contains("Math error")) {
                resultArea.setText(e.getMessage());
            } else resultArea.setText("Syntax error\n" + e.getMessage());
        } catch (Exception e) {
            resultArea.setDisabledTextColor(Color.RED);
            resultArea.setText("Math error\n" + e.getMessage());
        } finally {
            pack();
            System.out.println(convertedString);
        }
    }
}
