package hu.codeandsoda.osa.myshoebox.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import hu.codeandsoda.osa.account.user.domain.User;
import hu.codeandsoda.osa.exception.ValidationException;
import hu.codeandsoda.osa.general.data.ResponseMessage;
import hu.codeandsoda.osa.myshoebox.data.ImageData;
import hu.codeandsoda.osa.myshoebox.data.MyShoeBoxData;
import hu.codeandsoda.osa.myshoebox.domain.MyShoeBox;
import hu.codeandsoda.osa.myshoebox.repository.MyShoeBoxRepository;
import hu.codeandsoda.osa.pagination.service.PaginationService;
import hu.codeandsoda.osa.sort.service.SortMyShoeBoxService;
import hu.codeandsoda.osa.util.ErrorCode;

@Service
public class MyShoeBoxService {

    private static Logger logger = LoggerFactory.getLogger(MyShoeBoxService.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageDataService imageDataService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private SortMyShoeBoxService sortMyShoeBoxService;

    @Autowired
    private MyShoeBoxRepository myShoeBoxRepository;

    public MyShoeBox loadById(Long myShoeBoxId) {
        Optional<MyShoeBox> optionalMyShoeBox = myShoeBoxRepository.findById(myShoeBoxId);
        MyShoeBox myShoeBox = optionalMyShoeBox.isPresent() ? optionalMyShoeBox.get() : null;
        return myShoeBox;
    }

    public MyShoeBoxData loadUserMyShoeBox(Long myShoeBoxId, String sort, String sortBy, int size, int page) {
        MyShoeBox myShoeBox = loadById(myShoeBoxId);
        List<ImageData> imageDatas = imageDataService.constructImageDatas(myShoeBox.getImages());

        List<ResponseMessage> messages = new ArrayList<>();
        sortMyShoeBoxService.sortImages(imageDatas, sort, sortBy, messages, myShoeBoxId);

        Page<ImageData> imageDataPage = paginationService.createImageDataPage(imageDatas, size, page, messages, myShoeBoxId);
        MyShoeBoxData myShoeBoxData = new MyShoeBoxData.MyShoeBoxDataBuilder().setMessages(messages).setId(myShoeBoxId).setName(myShoeBox.getName()).setImages(imageDataPage)
                .build();
        return myShoeBoxData;
    }

    public ImageData loadImageByIdAndUser(Long id, User user, Errors errors) throws ValidationException {
        Long myShoeBoxId = user.getMyShoeBox().getId();
        ImageData image = null;
        if (imageService.userImageExists(id, myShoeBoxId)) {
            image = imageService.loadImageDataById(id);
        } else {
            String error = ErrorCode.IMAGE_NOT_FOUND.toString();
            String logMessage = new StringBuilder().append("action=loadImageById, status=error, userId=").append(user.getId()).append(", imageId=").append(id).append(", error=")
                    .append(error).toString();
            logger.error(logMessage);

            String errorMessage = "Could not load image";
            errors.reject(error, errorMessage);
            throw new ValidationException(errorMessage, errors.getAllErrors());
        }

        return image;
    }

    public int loadUsersMyShoeBoxSize(User user) {
        Long myShoeBoxId = user.getMyShoeBox().getId();
        int myShoeBoxSize = imageService.loadMyShoeBoxSize(myShoeBoxId);
        return myShoeBoxSize;
    }

}
