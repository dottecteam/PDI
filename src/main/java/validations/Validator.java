package validations;

import java.util.regex.Pattern;

class StringValidator {
	private static final Pattern PATTERN_DATE = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(20)\\d{2}$"); 		//valida o formato data, DD/MM/AAAA
	private static final Pattern PATTERN_NAME = Pattern.compile("^[A-Za-z][A-Za-z\\s]*[A-Za-z]$");								//valida nomes, apenas ASCII, não pode começar ou terminar com espaço
	private static final Pattern PATTERN_DESCRIPTION = Pattern.compile("^[^\\n]{10,255}$");                                     //valida a maior parte dos caracteres, minimo de caracteres 10 maximo 255
	private static final Pattern PATTERN_UNICODE = Pattern.compile("^[\\p{L}][\\p{L}\\s]*[\\p{L}]{10,255}$");					//valida todos os caracteres UNICODE, minimo de caracteres 10 maximo 255
    private static final Pattern PATTERN_CATEGORY = Pattern.compile("^[\\p{L}\\d][\\p{L}\\d\\s\\-]{2,50}[\\p{L}\\d]$");
	
	public static boolean dateValidate(String date) {
		return PATTERN_DATE.matcher(date).matches();
	}
	
	//Usar com nomes próprios, só aceita formato ASCII
	public static boolean nameValidate(String name) {
		return PATTERN_NAME.matcher(name).matches();
	}
	
	public static boolean descriptionValidate(String description) {
		return PATTERN_DESCRIPTION.matcher(description).matches();
	}
	
	//TODO alterar a função quando as categorias estiverem definidas
	//Por enquanto só valida os caracteres
	public static boolean categoryValidate(String category) {
		return PATTERN_UNICODE.matcher(category).matches(); //temporariamente como unicode
	}
}