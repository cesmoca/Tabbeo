/*
 * Copyright (c) 2001-2006 MUSIC TECHNOLOGY GROUP (MTG)
 *                         UNIVERSITAT POMPEU FABRA
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

#ifndef ChordCorrelator_hxx
#define ChordCorrelator_hxx

#include <vector>
#include <string>
#include <map>

// Logging
#include "jniUtils.h"

namespace Simac
{

  /**
   * Correlates PCP's with different ideal chord PCP's.
   * ChordCorrelator correlates an input Pitch Class Profile (PCP)
   * with a set stereotypic PCP for ideal chords having the energy 
   * concentrated on the pitches that form the chord.
   * The output is an array of the correlation for each chord model.
   * chordRepresentation(i), root(i) and mode(i) can be used to know
   * the chord which corresponds to the ith bin on the output.
   
   [EDIT] We changed the correlation algorithm.
   Now we give a score from [0, 120], 10 points for each note.
   -If the note belongs to the algorithm, we give somewhere between [0, 10],
   10 being the maximun intensity for that PCP
   -If the note does not belong to the algorithm, we give somewhere between [0, 10]
   10 being the note not being detected. We give 0 points if the note is detected 
   with maximun intensity
   - The correlationThreshold has a value between [0, 120] and sets the minimun to 
   say that a chord has been correctly detected

   However, to avoid noise, we do not take into account those notes detected with
   at least a fraction of the maximun intensity for that PCP. That threshold is set
   in componentThreshold, in the range [0, 1], indicating the minimun percentage of 
   the maximun intensity to be detected. For example, if the maximun frequency is
   100, and the componentThreshold is 0.2, a note detected with 30 would be taken
   into account, but a note with intensity 25 would be considered as 0.
  */
  class ChordCorrelator
  {
  public:
    // This order depends on how the PCP is formatted
    enum Pitch {G,Ab,A,Bb,B,C,Db,D,Eb,E,F,Gb};

    enum Mode {//Fifth, // 2 note chords
	       Major, Minor, Suspended2, Suspended4, Augmented, Diminished, // 3 notes chords
	       Sixth, Minor6, Seventh, Minor7, Major7, MinorMajor7, Diminished7}; // 4 notes chords

    struct Chord{
      Pitch pitch;
      Mode mode;

      Chord(const Pitch& p, const Mode& m): pitch(p), mode(m) {}

      std::string toString()
      {
	return getPitchName(pitch)+" "+getModeName(mode);
      }
    };
        
    ChordCorrelator(int correlationThreshold, double componentThreshold);

    double getMaxIntensity(){ return _maxPCP; }
    void doIt(const std::vector<double>& pcp);
    
    std::vector<ChordCorrelator::Chord*> getDetectedChords();

  private:
    struct ModePattern
    {
      const double templatePattern[12]; // This one is the "template"
      const unsigned nChordNotes;
      const unsigned nNotChordNotes;
    };

    struct ChordPattern {
      Chord chord;
      const ModePattern* modePattern;
      double pattern[12]; // This one is the shifted one so that the first element is the root
      int correlation;

      ChordPattern(Pitch p, Mode m, const ModePattern* mP): chord(p, m), modePattern(mP) { }

      // We sort in descending order (reverse), so we actually return the bigger one
      bool operator < (const ChordPattern& other) const
      {
	return correlation > other.correlation;
      }
      
    };

  private:
    std::vector<ChordPattern> _chordPatterns;
    const int _correlationThreshold;
    const double _componentThreshold;
    double _maxPCP;

    static const std::string& getPitchName(const Pitch& pitch);
    static const std::string& getModeName(const Mode& mode);
       
    static const std::map<Mode, ModePattern>& getModePatterns();
  };

} // namespace Simac

#endif// ChordCorrelator_hxx


