function checkEmailUnique(form) {

    /*url = "[[@{/users/check_email}]]";*/
    // Gigamit nato ang global variable imbis nga [[@...]]
    const userEmail = $("#email").val();
    const userId = $("#id").val();
    const csrfValue = $("input[name='_csrf']").val();

    params = { id: userId, email: userEmail, _csrf: csrfValue };

    // checkEmailUrl ang atong gamiton diri
    $.post(checkEmailUrl, params, function(response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "Duplicated") {
            showWarningModal("There is another user having the email: " + userEmail);
        } else {
            showErrorModal("Unknown response from server");
        }
    }).fail(function() {
        showErrorModal("Could not connect to the server");
    });

    return false; // Ayaw sa i-submit ang form
}


function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal('show');
}

function showErrorModal(message) {
	showModalDialog("Error", message)
}

function showWarningModal(message) {
	showModalDialog("Warning", message)
}

$(document).ready(function() {
    // Kani nga listener mo-detect kon naay gipili nga file ang user
    $("#fileImage").change(function() {
        // I-check kon ang file size dili ba sobra sa 1MB (Optional pero recommended ni Nam Ha Minh)
        fileSize = this.files[0].size;
        
        if (fileSize > 1048576) {
            this.setCustomValidity("You must choose an image less than 1MB!");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    });
});

function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    
    reader.onload = function(e) {
        // I-update ang 'src' sa imong img tag para makita ang preview
        $("#thumbnail").attr("src", e.target.result);
    };
    
    reader.readAsDataURL(file);
}

$(document).ready(function() {
    // I-call nato ang function inig load sa page
    customizeDropDownMenu();
});

function customizeDropDownMenu() {
	$(".navbar .dropdown").hover(
		function() {
			$(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
		},
		function() {
			$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
		}
	);
	
	$(".dropdown > a").click(function(){
		location.href = this.href;
	});
}

function checkPasswordMatch(confirmPassword) {
	if (confirmPassword.value != $("#password").val()) {
		confirmPassword.setCustomValidity("Password do not match!");
	} else {
		confirmPassword.setCustomValidity("");
	}
}

	/*check Uniqueness of Category*/
function checkUniqueCategory(form) {
    /*url = "[[@{/categories/check_unique}]]";*/
    catName = $("#name").val();
    catAlias = $("#alias").val();
    catId = $("#id").val();
    csrfValue = $("input[name='_csrf']").val();

    params = { id: catId, name: catName, alias: catAlias, _csrf: csrfValue };

    $.post(checkUniqueUrl, params, function(response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "DuplicateName") {
            showWarningModal("There is another category having same name: " + catName);
        } else if (response == "DuplicateAlias") {
            showWarningModal("There is another category having same alias: " + catAlias);
        } else {
			showErrorModal("Unknown response from server");
		}
	}).fail(function() {
		showErrorModal("Could not connect to the server");
    });

    return false;
}

$(document).ready(function() {
	var brandDropdownCategories = $("#categories");
	var divChosenCategories = $("#chosenCategories");
	
	brandDropdownCategories.change(function() {
		divChosenCategories.empty();
		showChosenCategories(brandDropdownCategories, divChosenCategories);
	});
	
		// I-call ni para kon naay data sa sugod (Edit Mode), makita ang badges
		showChosenCategories(brandDropdownCategories, divChosenCategories);
});


function showChosenCategories(dropdown, div) {
	dropdown.children("option:selected").each(function() {
		var selectedCategory = $(this);
		//catId = selectedCategory.val();
		var catName = selectedCategory.text().replace(/-/g, "");
		
		div.append("<span class='badge bg-secondary m-1'>" + catName + "</span>");
	});
}

	/*check Uniqueness of Brands*/
function checkUniqueBrand(form) {
    /*url = "[[@{/categories/check_unique}]]";*/
    brandId = $("#id").val();
    brandName = $("#name").val();
	
    csrfValue = $("input[name='_csrf']").val();

    params = { id: brandId, name: brandName, _csrf: csrfValue };

    $.post(checkUniqueUrl, params, function(response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "Duplicate") {
            showWarningModal("There is another brand having same name: " + brandName);
        
        } else {
			showErrorModal("Unknown response from server");
		}
		
	}).fail(function() {
		showErrorModal("Could not connect to the server");
    });

    return false;
}

// pag-load sa Categories inig pili nimo og Brand sa Product Form
// 1. I-update kini nga block para sa Product Form
$(document).ready(function() {
    var selBrand = $("#brand"); // Gigamitan og 'var' para dili global conflict
    var selCategory = $("#category");

    selBrand.on("change", function() {
        selCategory.empty();
        // I-pass nato ang saktong variables sa function
        fetchCategoriesForProduct(selBrand, selCategory);
    });
});

// 2. I-update ang function name ug logic
function fetchCategoriesForProduct(brandDropdown, categoryDropdown) {
    var brandId = brandDropdown.val();
    var url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function(responseJson) {
        $.each(responseJson, function(_index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(categoryDropdown);
        });
        // I-enable pagbalik ang dropdown
        categoryDropdown.removeAttr("disabled");
    });
}

// Richtext Editor
$(document).ready(function() {
    // I-initialize ang RichText para sa Short ug Full Description
    $("#shortDescription").richText();
    $("#fullDescription").richText();
});