package com.example.mobiletrainer;

public class Constants {

    /**Константы, используемые в приложении*/

    public static final String APP_PREFERENCES = "MobileTrainer_Settings";

    public static final String APP_PREFERENCES_FILE_COUNT = "MobileTrainer_FileCount";
    public static final String APP_NAME = "Mobile Trainer";
    public static final String APP_FOLDER_NAME = "MobileTrainer";
    public static final String NETWORK_FILE ="model.pt";


    public static final int HAND_ROTATION = 0;
    public static final int LUNGE = 1;
   // public static final int BENDS_WITH_HANDS =2;
    public static final int PUSH_UPS = 2;
    public static final int HAND_LIFTS = 3;
    public static final int PRESS = 4;
    public static final int SQUATTING =5;
    public static final int JUMPING =6;
    //public static final int HAND_HORIZONTAL_LIFTS = 7;
    //public static final int STRETCHING = 9;


    public static int[] EXERCISES_CLASSES = {
            HAND_ROTATION,
            LUNGE,
      //      BENDS_WITH_HANDS,
            PUSH_UPS,
            HAND_LIFTS,
            PRESS,
            SQUATTING,
            JUMPING,
    //        HAND_HORIZONTAL_LIFTS,
   //         STRETCHING
    };

}
