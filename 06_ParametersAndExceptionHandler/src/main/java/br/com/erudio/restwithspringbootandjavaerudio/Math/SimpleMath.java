package br.com.erudio.restwithspringbootandjavaerudio.Math;

public class SimpleMath {


    public Double sum(Double numberOne, Double numberTwo){
        return numberOne + numberTwo;
    }


    public Double less(Double numberOne, Double numberTwo)  {
        return numberOne - numberTwo;
    }


    public Double multi(Double numberOne, Double numberTwo) {
        return numberOne * numberTwo;
    }


    public Double div(Double numberOne, Double numberTwo) {
        return numberOne / numberTwo;
    }


    public Double average(Double numberOne, Double numberTwo) {
        return (numberOne + numberTwo) / 2;
    }



    public Double squareRoot(Double number)  {
        return Math.sqrt(number);
    }
}
