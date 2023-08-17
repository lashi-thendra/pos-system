package lk.ijse.dep10.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {


    private Integer id;

    @NotBlank(message = "Name can't be empty or null")
    @Pattern(regexp = "[A-Za-z]+", message = "Invalid name")
    private String name;

    @NotBlank(message = "Address can't be empty or null")
    @Length(min=3, message="Invalid address")
    private String address;

    @NotBlank(message = "Contact number can't be empty or null")
    @Pattern(regexp = "^\\d{3}-\\d{7}$", message = "Invalid contact number")
    private String contact;
}
