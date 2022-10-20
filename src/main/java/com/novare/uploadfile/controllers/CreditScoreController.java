//package com.novare.uploadfile.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//
//@RestController
//public class CreditScoreController {
//
//    @Autowired
//    CreditScoreRepository creditScoreRepository;
//
//    @GetMapping("/creditscores/{ssn}")
//    public CreditScore getCreditScore(@PathVariable("ssn") String ssn){
//        System.out.println(ssn);
//        return creditScoreRepository.findById(ssn).get();
//    }
//
//}
