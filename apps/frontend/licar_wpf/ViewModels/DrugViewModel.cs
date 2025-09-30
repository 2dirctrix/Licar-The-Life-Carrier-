using licar_wpf.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace licar_wpf.ViewModels
{
    public class DrugViewModel : ViewModelBase
    {
        private readonly Drug _drug;

        public int DrugId => _drug.DrugId;
        public string Name => _drug.Name;
        public int Dosage => _drug.Dosage;
        public string Unit => _drug.Unit;

        private bool _recognized;
        public bool Recognized
        {
            get => _recognized;
            set
            {
                _recognized = value;
                OnPropertyChanged();
            }
        }

        public DrugViewModel(Drug drug)
        {
            _drug = drug;
            _recognized = drug.Recognized;
        }
    }
}
