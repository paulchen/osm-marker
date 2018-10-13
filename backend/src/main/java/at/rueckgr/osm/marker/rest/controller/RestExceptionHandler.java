package at.rueckgr.osm.marker.rest.controller;

import at.rueckgr.osm.marker.rest.dto.ErrorResponse;
import at.rueckgr.osm.marker.rest.dto.ReturnCode;
import at.rueckgr.osm.marker.rest.dto.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class RestExceptionHandler {
    @RequestMapping(produces = "application/json")
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeExceededException(final MaxUploadSizeExceededException ex) {
        return new ErrorResponse(new StatusDTO(ReturnCode.FILE_TOO_LARGE, String.format("Maximum upload file size of %s bytes exceeded", ex.getMaxUploadSize())));
    }
}
