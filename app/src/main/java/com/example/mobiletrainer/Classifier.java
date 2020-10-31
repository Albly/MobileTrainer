package com.example.mobiletrainer;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.util.ArrayList;

public class Classifier {

    /**Класс с моделью нейросети и методом для предсказания*/

    Module model;

    long[] shape = new long[]{1,3,3,15};

    public static double PROBABILITY_THRESHOLD = 0.8;

    // Костыль! Убрать переменную
    public static double current_probability = 0.0;

    public Classifier(String modelPath){
        model = Module.load(modelPath);
    }

    public Tensor preprocess(float[] data){
        return Tensor.fromBlob(data,shape);
    }

    public int predict(float[] data){

        Tensor tensor = preprocess(data);
        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.forward(inputs).toTensor();
        float[] scores = outputs.getDataAsFloatArray();
//        int classIndex = argMax(scores);
        int classIndex = argMaxCrossComparison(scores);

        if(classIndex==-1){
            return -1;
        }
        return Constants.EXERCISES_CLASSES[classIndex];

    }

    public int argMaxCrossComparison(float[] inputs){
        int maxIndex = -1;
        float maxvalue = 0.0f;
        for (int i = 0; i < inputs.length; i++){
            if(inputs[i] > maxvalue ) {
                if(inputs[i] > PROBABILITY_THRESHOLD){
                    maxIndex = i;
                }
                maxvalue = inputs[i];
            }
        }
        current_probability = maxvalue;
        return maxIndex;
    }

    public int argMax(float[] inputs){
        int maxIndex = -1;
        float maxvalue = 0.0f;
        for (int i = 0; i < inputs.length; i++){
            if(inputs[i] > maxvalue) {
                maxIndex = i;
                maxvalue = inputs[i];
                //current_probability = maxvalue;
            }
        }
        return maxIndex;
    }

}
