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
    public class RecognitionToBrushConverter : IValueConverter
    {
        private static readonly Brush RecognizedBrush = new SolidColorBrush(Color.FromRgb(220, 255, 220));
        private static readonly Brush UnrecognizedBrush = new SolidColorBrush(Color.FromRgb(225, 220, 220));
        private static readonly Brush TransparentBrush = Brushes.Transparent;

        public object Convert(object value, Type targetType, object parameter, CultureInfo cultureInfo)
        {
            if (value is bool isRecognized)
            {
                return isRecognized ? RecognizedBrush : UnrecognizedBrush;
            }

            return TransparentBrush;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo cultureInfo)
        {
            throw new NotImplementedException();
        }
    }
}
