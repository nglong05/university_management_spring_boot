import random

def remove_accents(text):
    vietnamese_map = {'à':'a','á':'a','ả':'a','ã':'a','ạ':'a','ă':'a','ằ':'a','ắ':'a','ẳ':'a','ẵ':'a','ặ':'a','â':'a','ầ':'a','ấ':'a','ẩ':'a','ẫ':'a','ậ':'a','đ':'d','è':'e','é':'e','ẻ':'e','ẽ':'e','ẹ':'e','ê':'e','ề':'e','ế':'e','ể':'e','ễ':'e','ệ':'e','ì':'i','í':'i','ỉ':'i','ĩ':'i','ị':'i','ò':'o','ó':'o','ỏ':'o','õ':'o','ọ':'o','ô':'o','ồ':'o','ố':'o','ổ':'o','ỗ':'o','ộ':'o','ơ':'o','ờ':'o','ớ':'o','ở':'o','ỡ':'o','ợ':'o','ù':'u','ú':'u','ủ':'u','ũ':'u','ụ':'u','ư':'u','ừ':'u','ứ':'u','ử':'u','ữ':'u','ự':'u','ỳ':'y','ý':'y','ỷ':'y','ỹ':'y','ỵ':'y','À':'A','Á':'A','Ả':'A','Ã':'A','Ạ':'A','Ă':'A','Ằ':'A','Ắ':'A','Ẳ':'A','Ẵ':'A','Ặ':'A','Â':'A','Ầ':'A','Ấ':'A','Ẩ':'A','Ẫ':'A','Ậ':'A','Đ':'D','È':'E','É':'E','Ẻ':'E','Ẽ':'E','Ẹ':'E','Ê':'E','Ề':'E','Ế':'E','Ể':'E','Ễ':'E','Ệ':'E','Ì':'I','Í':'I','Ỉ':'I','Ĩ':'I','Ị':'I','Ò':'O','Ó':'O','Ỏ':'O','Õ':'O','Ọ':'O','Ô':'O','Ồ':'O','Ố':'O','Ổ':'O','Ỗ':'O','Ộ':'O','Ơ':'O','Ờ':'O','Ớ':'O','Ở':'O','Ỡ':'O','Ợ':'O','Ù':'U','Ú':'U','Ủ':'U','Ũ':'U','Ụ':'U','Ư':'U','Ừ':'U','Ứ':'U','Ử':'U','Ữ':'U','Ự':'U','Ỳ':'Y','Ý':'Y','Ỷ':'Y','Ỹ':'Y','Ỵ':'Y',}
    result = ''
    for char in text:
        result += vietnamese_map.get(char, char)
    return result

def generate_student_code(year, dept_code, index):
    """Generate MSV: B{year}DC{2 chars of dept}{3 digit index}"""
    dept_short = dept_code[:2].upper()
    return f"B{year}DC{dept_short}{index:03d}"

def generate_lecturer_code(dept_code, index):
    dept_short = dept_code[:2].upper()
    return f"GV{dept_short}{index:03d}"

def generate_email(ho, ten_dem, ten, year, dept_code, index, region='b'):
    """Generate email: {ten}{2 chars ho+tendem}.{region}{year}{dept_short}{index}@stu.ptit.edu.vn"""
    ten_no_accent = remove_accents(ten).lower()
    ho_short = remove_accents(ho)[0].lower()
    ten_dem_short = remove_accents(ten_dem)[0].lower() if ten_dem else ''
    dept_short = dept_code[:2].lower()
    
    email = f"{ten_no_accent}{ho_short}{ten_dem_short}.{region}{year}{dept_short}{index:03d}@stu.ptit.edu.vn"
    return email

def generate_lecturer_email(ho, ten_dem, ten, dept_code, index):
    ten_no_accent = remove_accents(ten).lower()
    ho_short = remove_accents(ho)[0].lower()
    ten_dem_short = remove_accents(ten_dem)[0].lower() if ten_dem else ''
    dept_short = dept_code[:2].lower()
    
    email = f"{ten_no_accent}{ho_short}{ten_dem_short}.{dept_short}{index:03d}@lt.ptit.edu.vn"
    return email

def generate_birth_date(year):
    """Generate random birth date for student"""
    # Students typically 18-19 years old when entering
    birth_year = int(f"20{year}") - 18 - random.randint(0, 2)
    month = random.randint(1, 12)
    day = random.randint(1, 28)
    return f"{birth_year}-{month:02d}-{day:02d}"

def generate_phone():
    """Generate random Vietnamese phone number"""
    prefixes = ['032', '033', '034', '035', '036', '037', '038', '039', 
                '096', '097', '098', '086', '091', '094', '088', '083', '084', '085', '081', '082']
    return random.choice(prefixes) + ''.join([str(random.randint(0, 9)) for _ in range(7)])

import random

def generate_address():
    """Generate a random Vietnamese-style address"""
    house_no = f"Số {random.randint(1, 500)}"
    alley = f"ngõ {random.randint(1, 200)}" if random.random() < 0.7 else ""
    sub_alley = f"ngách {random.randint(1, 50)}" if alley and random.random() < 0.3 else ""
    street = f"đường {random.choice(['Láng', 'Giải Phóng', 'Trường Chinh', 'Tây Sơn', 'Khâm Thiên', 'Cầu Giấy', 'Xuân Thủy', 'Hoàng Quốc Việt'])}"
    ward = f"phường {random.choice(['Trung Hòa', 'Dịch Vọng', 'Khương Mai', 'Quang Trung', 'Thổ Quan', 'Láng Hạ', 'Việt Hưng'])}"
    district = f"quận {random.choice(['Cầu Giấy', 'Đống Đa', 'Long Biên', 'Thanh Xuân', 'Hoàng Mai'])}"
    city = "Hà Nội"

    parts = [house_no]
    if alley:
        parts.append(alley)
    if sub_alley:
        parts.append(sub_alley)
    parts.extend([street, ward, district, city])

    return ", ".join(parts)