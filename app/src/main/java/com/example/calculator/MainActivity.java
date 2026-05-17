package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;
    private final StringBuilder input = new StringBuilder();
    private Double firstValue = null;
    private String operator = null;
    private boolean justEvaluated = false;
    private final DecimalFormat df = new DecimalFormat("0.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        int[] digitIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : digitIds) {
            findViewById(id).setOnClickListener(v -> {
                if (justEvaluated && operator == null) clearAll();
                Button b = (Button) v;
                appendDigit(b.getText().toString());
            });
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> {
            if (justEvaluated && operator == null) clearAll();
            appendDot();
        });

        findViewById(R.id.btnAdd).setOnClickListener(v -> onOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> onOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> onOperator("×"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> onOperator("÷"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> onOperator("%"));

        findViewById(R.id.btnEq).setOnClickListener(v -> onEquals());
        findViewById(R.id.btnC).setOnClickListener(v -> backspace());
        findViewById(R.id.btnAC).setOnClickListener(v -> clearAll());
        findViewById(R.id.btnOff).setOnClickListener(v -> finish());
    }

    private void appendDigit(String d) {
        if (input.length() == 1 && input.charAt(0) == '0' && !input.toString().contains(".")) {
            input.setLength(0);
        }
        input.append(d);
        tvResult.setText(input.toString());
    }

    private void appendDot() {
        if (input.length() == 0) {
            input.append("0.");
        } else if (!input.toString().contains(".")) {
            input.append(".");
        }
        tvResult.setText(input.toString());
    }

    private void backspace() {
        if (justEvaluated && operator == null) {
            clearAll();
            return;
        }
        if (input.length() > 0) input.deleteCharAt(input.length() - 1);
        tvResult.setText(input.length() == 0 ? "0" : input.toString());
    }

    private void clearAll() {
        input.setLength(0);
        firstValue = null;
        operator = null;
        justEvaluated = false;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    private void onOperator(String op) {
        if (input.length() == 0) {
            if (firstValue != null) {
                operator = op;
                tvExpression.setText(df.format(firstValue) + " " + operator);
            }
            justEvaluated = false;
            return;
        }

        double current = parseInput();

        if (firstValue == null) {
            firstValue = current;
        } else if (operator != null) {
            Double res = compute(firstValue, current, operator);
            if (res == null) {
                showError();
                return;
            }
            firstValue = res;
        } else {
            firstValue = current;
        }

        operator = op;
        input.setLength(0);
        tvExpression.setText(df.format(firstValue) + " " + operator);
        tvResult.setText("0");
        justEvaluated = false;
    }

    private void onEquals() {
        if (firstValue == null || operator == null || input.length() == 0) return;

        double second = parseInput();
        Double res = compute(firstValue, second, operator);
        if (res == null) {
            showError();
            return;
        }

        tvExpression.setText(df.format(firstValue) + " " + operator + " " + df.format(second) + " =");
        tvResult.setText(df.format(res));

        firstValue = res;
        operator = null;
        input.setLength(0);
        justEvaluated = true;
    }

    private double parseInput() {
        String s = input.toString();
        if (s.isEmpty() || s.equals(".")) return 0.0;
        return Double.parseDouble(s);
    }

    private Double compute(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "×": return a * b;
            case "÷": return b == 0.0 ? null : a / b;
            case "%": return (a * b) / 100.0;
            default: return null;
        }
    }

    private void showError() {
        tvExpression.setText("");
        tvResult.setText("Error");
        input.setLength(0);
        firstValue = null;
        operator = null;
        justEvaluated = false;
    }
}