package it.si2001.rentalcar.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class PrettyLogger {

    public ResponseEntity<ObjectNode> prettyException(Exception e, Logger logger, ObjectNode responseNode) {

        if(e.getCause() != null){

            if(e.getCause().getCause() != null){

                if(e.getCause().getCause() instanceof SQLException){

                    logger.error("***** "+e.getCause().getCause()+" *****");

                    responseNode.put("error", "Server error");

                    return new ResponseEntity<>(responseNode, HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }

        }

        logger.error("***** "+e.getMessage()+" *****");

        responseNode.put("error", "Bad request");

        return new ResponseEntity<>(responseNode, HttpStatus.BAD_REQUEST);

    }

}
