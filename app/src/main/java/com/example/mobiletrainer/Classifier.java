package com.example.mobiletrainer;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.util.ArrayList;

public class Classifier {

    /**Класс с моделью нейросети и методом для предсказания*/

    Module model;

    //Размеры вектора
    long[] shape = new long[]{1,3,3,15};

    // Порог вероятности при котором принимается положительное решение
    public static double PROBABILITY_THRESHOLD = 0.83;

    // Максимальная вероятность урпажения
    public static double current_probability = 0.0;

    //Загрузка модели
    public Classifier(String modelPath){
        model = Module.load(modelPath);
    }

    // Преобразование вектора к нужному размеру
    public Tensor preprocess(float[] data){
        return Tensor.fromBlob(data,shape);
    }

    //Прогоняем вектор через модель
    public int predict(float[] data){

        //Переобразуем данные к нужному формату
        Tensor tensor = preprocess(data);
        // Прогоняем вектор через модель
        IValue inputs = IValue.from(tensor);
        Tensor outputs = model.forward(inputs).toTensor();
        //Записываем результаты классификации
        float[] scores = outputs.getDataAsFloatArray();
        //Обрабатываем результат классификации
        int classIndex = argMaxCrossComparison(scores);

        if(classIndex==-1){
            // Возвращаем отсутствие упражнения
            return -1;
        }
        // Возвращаем упражнение
        return Constants.EXERCISES_CLASSES[classIndex];

    }


    public int argMaxCrossComparison(float[] inputs){
        /**Обработка результата сети*/
        //  Индекс макимального значения и его значение
        int maxIndex = -1;
        float maxvalue = 0.0f;

        //Пробегаем по всем элементам массива
        for (int i = 0; i < inputs.length; i++){
            //Если найдено новое максимальное значение
            if(inputs[i] > maxvalue ) {
                // Проверяем больше ли оно порогового значения, при котором принимается положительное решение
                if(inputs[i] > PROBABILITY_THRESHOLD){
                    // Записываем максимальный индекс
                    maxIndex = i;
                }
                // Записываем максимальное значение
                maxvalue = inputs[i];
            }
        }
        // Вероятность того, что было выполненно конкретное упражнение
        current_probability = maxvalue;
        return maxIndex;
    }

}
