package com.app.rekog.beans;

import java.util.ArrayList;

/**
 * Created by bkhera on 2/21/2018.
 */

public class ResultBean {

    public ArrayList<ImagesBean> images = new ArrayList<>();

    public class ImagesBean {

        public ArrayList<CandidatesBean> candidates = new ArrayList<>();

        public TransactionBean transaction;

        public class CandidatesBean {
            //            public double confidence;
            public String enrollment_timestamp, face_id, subject_id;
        }

        public class TransactionBean {

            /*"transaction": {
        "confidence": 0.9950643,
        "enrollment_timestamp": "1519193788140",
        "eyeDistance": 49,
        "face_id": "68adde249ad642c6b85",
        "gallery_name": "Test_Face_Images",
        "height": 107,
        "pitch": -24,
        "quality": 0.80331,
        "roll": 0,
        "status": "success",
        "subject_id": "Test 1",
        "topLeftX": 46,
        "topLeftY": 90,
        "width": 107,
        "yaw": 2
      }*/
            public String gallery_name, face_id, status, subject_id;

        }
    }


}
