package com.example.voicerec;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    TextView workingsTV;
    TextView resultsTV;

    Button btnMicOrCalc;
    Button btnMicOrCalc2;
    boolean leftBracket = false;
    String workings = "";
    String formula = "";
    String tempFormula = "";

    Double result=null;
    DbHelper dbHelper;

    boolean isIconCalc = false;
    Toolbar toolbar;
    MenuItem itemhist;
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTextViews();
        textToSpeech = new TextToSpeech(this, this);
        dbHelper=new DbHelper(this);

        // Setting toolbar:
        toolbar=findViewById(R.id.toolbarId);
        toolbar.setTitle("CompuVoice");

        setSupportActionBar(toolbar);

        itemhist=(MenuItem)findViewById(R.id.historyIcId);

        // Setting Microphone Fragment:
        setMicrophoneFragment();

        workingsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               setCalculatorFragment();
            }
        });
    }

    private  void setMicrophoneFragment()
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ftransaction=fm.beginTransaction();
        MicrophoneFragment microphoneFragment =new MicrophoneFragment();
        ftransaction.replace(R.id.FrameLay,microphoneFragment,"microphoneFragment");
        ftransaction.commit();

        isIconCalc=true;
        btnMicOrCalc.setBackgroundResource(R.drawable.calculatoricon);
    }

    private void initTextViews()
    {
        btnMicOrCalc=findViewById(R.id.micOrCalcBtn);
        btnMicOrCalc2=findViewById(R.id.micOrCalcBtn2);
        workingsTV = (TextView)findViewById(R.id.workingsTextView);
        resultsTV = (TextView)findViewById(R.id.resultTextView);

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(workingsTV,12,100,2, TypedValue.COMPLEX_UNIT_DIP);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(resultsTV,12,100,2, TypedValue.COMPLEX_UNIT_DIP);

    }

    public void setWorkings(String givenValue)
    {
        workings = workings + givenValue;
        workingsTV.setText(workings);
    }

    public void setNewWorkings(String givenvalue)
    {
        workings=givenvalue;
        workingsTV.setText(workings);
        resultsTV.setText("");
    }
    public void viewHistory()
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ftransaction = fm.beginTransaction();
        FragmentHistory fragmentHistory = new FragmentHistory();
        ftransaction.replace(R.id.FrameLay, fragmentHistory, "fragmentHistory");
        ftransaction.addToBackStack(null);
        ftransaction.commit();
    }

    public void viewCalculator(View view)
    {
        setBtn2IconInvisible();
        if(!isIconCalc)
        {
            setMicrophoneFragment();
        }
        else
        {
            setCalculatorFragment();
        }
    }
    public void viewCalculator2(View view)
    {
        if(isIconCalc)
        {
            setMicrophoneFragment();
            btnMicOrCalc.setBackgroundResource(R.drawable.calculatoricon);
        }
        else
        {
            setCalculatorFragment();
        }
        setBtn2IconInvisible();
    }
    public void setCalculatorFragment()
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ftransaction=fm.beginTransaction();
        CalculatorFragment calculatorFragment =new CalculatorFragment();
        ftransaction.replace(R.id.FrameLay,calculatorFragment,"fragementCalculator");
        ftransaction.commit();
        btnMicOrCalc.setBackgroundResource(R.drawable.microphone);
        isIconCalc=false;
    }

    public void backSpaceOnClick(View view)
    {
        if(!workings.isEmpty()) {
            StringBuilder sb = new StringBuilder(workings);
            workings = sb.deleteCharAt(workings.length() - 1).toString();
            workingsTV.setText(workings);
        }
    }
    public void equalsOnClick(View view)
    {
        if(!workings.isEmpty()) {
            Calculate();
            if (result != null) {
                resultsTV.setText(Float.toString(result.floatValue()));

                dbHelper.addOperation(new Operation(workingsTV.getText().toString(), Float.toString(result.floatValue())));
            }
        }
    }
    public void Calculate()
    {
        result=null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        formula = workings;

        if(formula.equals("//"))
        {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }
            checkForPowerOf();
            try {
                result = (double) engine.eval(formula);

            } catch (ScriptException e) {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            }
    }


    private void checkForPowerOf()
    {
        ArrayList<Integer> indexOfPowers = new ArrayList<>();
        for(int i = 0; i < workings.length(); i++)
        {
            if (workings.charAt(i) == '^')
                indexOfPowers.add(i);
        }

        tempFormula = workings;
        for(Integer index: indexOfPowers)
        {
            changeFormula(index);
        }
        formula = tempFormula;
    }

    private void changeFormula(Integer index)
    {
        int i=0,j=0;
        String numberLeft = "";
        String numberRight = "";
        String changed="";
        boolean minusDetected=false;

        for( i= index+1; i< workings.length(); i++) {
            if (i == index + 1) {
                if (workings.charAt(i) == '-')
                {
                    minusDetected = true;
                    i++;
                }
            }
                if (isNumeric1(workings.charAt(i)))
                    numberRight = numberRight + workings.charAt(i);
                else
                    break;

        }
        for( j = index-1; j >= 0; j--)
        {
            if(isNumeric1(workings.charAt(j)))
                numberLeft =  workings.charAt(j)+numberLeft ;
            else
                break;
        }

        String original = numberLeft + "^" + numberRight;
        if(!minusDetected)
         changed = "Math.pow("+numberLeft+","+numberRight+")";
        else {
            original = numberLeft + "^" +"-"+ numberRight;
            changed = "1/("+ "Math.pow("+numberLeft+","+numberRight+"))";
            minusDetected=false;
        }
        tempFormula = tempFormula.replace(original,changed);
    }


    private  boolean isAlphabetic3(String str) {
        int i = 0, type = -1;
        boolean bool = false;
        Character c = new Character('?');
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                if(Character.isLetter(c))
                    bool=true;
            }
        }
        return bool;
    }
    private boolean isNumeric1(char c)
    {
      //  if((Integer.parseInt(Character.toString(c)) <= 9 && Integer.parseInt(Character.toString(c)) <= 9) || Character.toString(c).equals("."))
         if(c <= '9' && c>= '0' || c == '.' || c==' ')
            return true;
        return false;
    }

    public void clearOnClick(View view)
    {
        workingsTV.setText("");
        workings = "";
        resultsTV.setText("");
        leftBracket = false;
    }

    public void bracketsOnClick(View view)
    {
        if(!leftBracket)
        {
            setWorkings("(");
            leftBracket = true;
        }
        else
        {
            setWorkings(")");
            leftBracket = false;
        }
    }

    public void powerOfOnClick(View view)
    {
        setWorkings("^");
    }

    public void divisionOnClick(View view)
    {
        setWorkings("/");
    }

    public void sevenOnClick(View view)
    {
        setWorkings("7");
    }

    public void eightOnClick(View view)
    {
        setWorkings("8");
    }

    public void nineOnClick(View view)
    {
        setWorkings("9");
    }

    public void timesOnClick(View view)
    {
        setWorkings("*");
    }

    public void fourOnClick(View view)
    {
        setWorkings("4");
    }

    public void fiveOnClick(View view)
    {
        setWorkings("5");
    }

    public void sixOnClick(View view)
    {
        setWorkings("6");
    }

    public void minusOnClick(View view)
    {
        setWorkings("-");
    }

    public void oneOnClick(View view)
    {
        setWorkings("1");
    }

    public void twoOnClick(View view)
    {
        setWorkings("2");
    }

    public void threeOnClick(View view)
    {
        setWorkings("3");
    }

    public void plusOnClick(View view)
    {
        setWorkings("+");
    }

    public void decimalOnClick(View view)
    {
        setWorkings(".");
    }

    public void zeroOnClick(View view)
    {
        setWorkings("0");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if(resultCode==RESULT_OK) {
                ArrayList<String> lol = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String g = lol.get(0);

                String[] all = g.split(" ");
          //      String sum = " ";
                String summ = "";

                for (int i = 0; i < all.length; i++) {
                    all[i].toLowerCase();
                }

                convert(all);

                for (int i = 0; i < all.length; i++) {
                    summ += all[i];
                }
                summ = summ.trim();

                formula = summ;

                if (isAlphabetic3(formula)) {
                    textToSpeech.speak("Only numeric inputs are allowed", TextToSpeech.QUEUE_ADD, null);
                    setNewWorkings("");
                    return;
                }
                workings = summ;
                workingsTV.setText(summ);
                // Calculate directly:
                Calculate();

                if (result != null)
                {
                    textToSpeech.speak(String.valueOf(result.floatValue()), TextToSpeech.QUEUE_ADD, null);
                    resultsTV.setText(Float.toString(result.floatValue()));

                    //Add operation to history:
                    dbHelper.addOperation(new Operation(workingsTV.getText().toString(), Float.toString(result.floatValue())));
                } else {
                    result = null;
                    resultsTV.setText("");
                    Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();
                }
            }
            else
                {
                    if (resultCode==RESULT_CANCELED)
                        return;
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"sorry,I didn't catch that! please try again",Toast.LENGTH_LONG).show();
            return;
        }
    }
    private void convert(String Tab[]){
        for(int i=0;i<Tab.length;i++){
            switch(Tab[i]){
                case "zero":
                    Tab[i]="0";
                    break;
                case "one":
                    Tab[i]="1";
                    break;
                case "two":
                    Tab[i]="2";
                    break;
                case "three":
                    Tab[i]="3";
                    break;
                case "four":
                    Tab[i]="4";
                    break;
                case "five":
                    Tab[i]="5";
                    break;
                case "six":
                    Tab[i]="6";
                    break;
                case "seven":
                    Tab[i]="7";
                    break;
                case "eight":
                    Tab[i]="8";
                    break;
                case "nine":
                    Tab[i]="9";
                    break;
                case "plus":
                case "add":
                case "+":
                    Tab[i]="+";
                    break;
                case "minus":
                case "subtract":
                case "-":
                    Tab[i]="-";
                    break;
                case "times":

                case "multiply":
                case "multiplied by":
                case "*":
                case "x":
                    Tab[i]="*";
                    break;
                case "times minus":
                    Tab[i]="*-";
                    break;
                case "divided by":
                case "divide":
                case "/":
                    Tab[i]="/";
                    break;
                case "power":
                case "powers":
                case "-power":
                case "raised to":
                case "power of":
                case "^":
                case "^ of":
                    Tab[i]="^";
                    break;
                case "2-power":
                    Tab[i]="2^";
                    break;
                case "÷":
                    Tab[i]="/";
                    break;
                case "bracket":
                case "brackets":
                case "parentheses":
                case "parenthese" :
                {
                    if(!leftBracket)
                    {
                        Tab[i] = "(";
                        leftBracket=true;
                    }
                    else {
                        Tab[i] = ")";
                        leftBracket=false;
                    }
                    break;
                }
                default:
            }
        }
    }
    public  boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mymenu,menu);
        return true;
    }

    public void setBtn2Icon()
    {
       if(!isIconCalc)
           btnMicOrCalc2.setBackgroundResource(R.drawable.calculatoricon);
       else
       {
           btnMicOrCalc2.setBackgroundResource(R.drawable.microphone);
       }
       btnMicOrCalc2.setVisibility(View.VISIBLE);
    }
    public void setBtn2IconInvisible()
    {
        btnMicOrCalc2.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.historyIcId:
                viewHistory();
                return super.onOptionsItemSelected(item);

            default:
                 return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onInit(int status) {

    }
}