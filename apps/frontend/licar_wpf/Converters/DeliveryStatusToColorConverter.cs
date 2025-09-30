using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Data;
using System.Windows.Media;

namespace licar_wpf.Converters
{
    public class StatusToBrushConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string status = value as string;
            if (string.IsNullOrEmpty(status))
            {
                return Brushes.Black;
            }

            switch (status.ToLower())
            {
                case "completed":
                    return Brushes.Green;
                case "in_transit":
                    return Brushes.DodgerBlue;
                case "requested":
                    return Brushes.Orange;
                default:
                    return Brushes.Gray;
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
