package com.example.voicerec;

import androidx.annotation.NonNull;

public class Operation {
        int Id;
        String Formula;
        String Result;

        public Operation()
        {

        }

        public Operation (int id,String formula,String result)
        {
            this.Id=id;
            this.Formula=formula;
            this.Result=result;
        }

        public Operation(String formula,String result)
        {
            this.Formula=formula;
            this.Result=result;
        }

        public int getId() { return Id; }

        public void setId(int id) { Id = id;}

        public String getFormula(){ return Formula; }

        public void setFormula(String formula) { Formula = formula; }

        public String getResult() { return Result; }

        public void setResult(String result) { Result = result; }

        @NonNull
        @Override
        public String toString() { return this.Formula+" = "+this.Result; }
    }
