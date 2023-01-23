$(document).ready(function() {
    $(document).on('submit','form#new_form',function(e) {
       e.preventDefault();
	   var validationUrl = '/bin/saveUserDetails?age=' + $('[name="age"]').val();
       
	   $.ajax({
          url: validationUrl,
          success: function(data) {
            if(data == 'ok')
				var formData = {
					'firstName': $('[name="first-name"]').val(),
					'lastName': $('[name="last-name"]').val(),
					'age': $('[name="age"]').val(),
					'country': $('[name="country"]').val()
				   }
                saveUser(formData);
            else
                alert("You are not eligible");
          }
        });
    });

    function saveUser(formData) {        
        $.ajax({
          url: '/bin/saveUserDetails',
          method: 'POST',
          data: formData,
          success: function(data) {
			alert("Thank you");
          }
        });
    }
});