using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;

namespace licar_wpf.Converters
{
    public class StatusToKoreanConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo cultureInfo)
        {
            string status = value as string;
            if (string.IsNullOrEmpty(status))
            {
                return string.Empty;
            }

            switch (status)
            {
                case "requested":
                    return "요청";
                case "in_transit":
                    return "전송중";
                case "completed":
                    return "도착";
                default:
                    return status;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo cultureInfo)
        {
            throw new NotImplementedException();
        }
    }
}
