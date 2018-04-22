package mobile.and.web.iti.navigationdrawer.update.view.trips;

/**
 * Created by Alaa on 2/24/2018.
 */

public class Users {
    int image ;
    String name ;
    String address;
    public Users(int image , String name , String address){
        this.image = image;
        this.name = name;
        this.address=address;


    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}

